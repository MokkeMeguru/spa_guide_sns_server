# 実装時に考えていたこと 2

## サーバ と CQRS を意識した GraphQL mutation の定義

GraphQL を考えたときに Mutation には何らかのモデルを返すことが望まれている。
一般的かどうかはさておき、多くのサンプルプロジェクトでは Mutation の返り値に Query 相当のモデル / Query 相当のモデルを内包したモデルが用いられることが多い。

ここに サーバ事情 と CQRS のエッセンスをひとつまみすると、ちょっと困ったことが起こる。
以下の例を考える

```graphql
type Mutation {
    regiterUser(input: {...}): RegisterUserPayload!
}

type RegisterUserPayload {
    user: UserQuery!
}

type UserQuery {
    id: ID!
    name: String!
    communities: [Community!]!
    tweets: [Tweet!]!
}
```

このとき、 UserQuery には所属コミュニティ情報やすべてのツイート情報などの情報が含まれてしまう。
ところがサーバ上では、必ずしもユーザ作成時にコミュニティを舐めるわけでも Tweet 情報を舐めるわけではないので、
サーバのコストはかさみ、Query を組み立てる際のエラーハンドリングについて考える必要が出てくる

もっとわかりやすい例では

```graphql
type Mutation {
    tweet(input: {...}): TweetPayload
}

type TweetPayload {
    tweet: TweetQuery!
}

type TweetQuery {
    id: ID!
    text: String!
    user: UserQuery!
}
```

このときクライアントが以下のような設計をしたときを想像してみる

```graphql
mutation CreateTweet($input: {...}) {
    tweet(input: $input) {
        tweet {
            id
            text
            user {
                id
                name
                communities {
                    ...
                }
                tweets {
                    ....
                }
            }
        }
    }
}
```

ただ Tweet をするだけなのに、容易にサーバが爆発することが容易に想像できる

これを防ぐには、クライアントが **暗黙的に** サーバの負担を考えて **本来できる** クエリ設計を見直す必要が出てくる。
(本来 GraphQL はクライアントとサーバを粗結合にするメリットが推されていたはずなのに! / この調整のためのクライアント <-> サーバコミュニケーションコストは GraphQL モデルのコストに対して割に合わなすぎる)

また、Tweet はできているはずなのに、 communities の一つが DB の都合で検索できなかったとき、ユーザ情報が取れなかったとき、サーバがどのようなレスポンスを返すべきかという問題が発生する。
解決策として `TweetPayload > tweet` を optional にするという案もあるが、 optional な値の場合、**クライアントの処理分岐が増える** という欠点がある。

一般的なクライアントでの認識としてそうなのかは不明だが、一般にサーバでは、データ作成をしたら必ずレスポンスのモデルが作成できるわけではない。
特に GraphQL の query のような複雑なモデルであればなおさら保証できない。

https://www.apollographql.com/blog/graphql/basics/designing-graphql-mutations/

例えば Appollo Blog では、Todo モデルの作成 `createTodo` についてのレスポンス `CreateTodoPayload` について

1. Todo モデルが作成できたら `CreateTodoPayload>Todo` は必ず含まれる
2. Todo モデルが作成できなければ `CreateTodoPayload>Todo` は含まれない

という分岐が想定されているが、実際は

1. Todo モデルが作成できて、Todo モデルを組み立てられれば `CreateTodoPayload>Todo` は必ず含まれる
2. Todo モデルが作成できて、Todo モデルを組み立てられなかったとき <undefined>
3. Todo モデルが作成できなければ `CreateTodoPayload>Todo` は含まれない

となる。
そして特に複雑なクエリを許容すればするほど、2. について無視することは難しい。

## 今回のプロジェクトではどうしたか

Twitter API v2 を参考にレスポンスのためのモデルを作成することにした。

https://developer.twitter.com/en/docs/twitter-api/tweets/manage-tweets/api-reference/post-tweets

```graphql
type Mutation {
    tweet(input: {...}): TweetPayload!
}

type TweetPayload {
    id: ID!
    text: String!
}
```

## 反対意見: GitHub GraphQL

GitHub の GraphQL では例えば次のように Optional を利用してレスポンスを定義している。

https://docs.github.com/ja/graphql/overview/public-schema

```graphql
type CreatePullRequestPayload {
  """
  A unique identifier for the client performing the mutation.
  """
  clientMutationId: String

  """
  The new pull request.
  """
  pullRequest: PullRequest
}
```

## 反対意見: Spotify

Spotify の GraphQL 作成のノウハウでは、Optional を利用しつつ、REST-API でいうステータスコードを返すことで解決を目指している
が、status code で分岐する画面設計をするコストをかけるなら、はじめからレスポンスモデルがない前提で作ったほうが効率がよいのではないか、というところもある
(うまくやるなら Payload に作成したモデルの ID を non-null 型で含め、仮に mutation レスポンスで succeed + モデルが取得できなかったときには、クライアント側でリトライするなどが考えられる / そこまでの各部署のエンジニアがリソースを割けるかは別)

https://graphql-rules.com/rules/mutation-payload-status

```graphql
type CreatePersonPayload {
  record: Person
  status: CreatePersonStatus! # fail, success, etc. Or 201, 403, 404 etc.
  # ... any other fields you like
}
```

余談: 体力があれば Spotify パターンの実装をしたいなぁ
