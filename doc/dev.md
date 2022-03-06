# 開発者向けドキュメント

## プロジェクトの初期化

```
npm install -g npm
npx create-cljs-project spa_guide_sns_server
```

## プロジェクトのリリース

```
npm release
```

### リリースされたプロジェクトの実行

```
npm start_release
```

## project development

### テスト実行

- 自動テスト

コード変更時に自動でテストが走ります

```
npm run watch_test
```

- ci テスト

全てのテストが通っていれば 0, そうでなければ 1 がステータスコードで返ります

```
npx shadow-cljs compile node-test && node out/node-tests.js
```

### for emacs (cider)

run `cider-jack-in-cljs`

### for vscode (calva)

TODO

## 利用しているライブラリ

- shadow-cljs https://github.com/thheller/shadow-cljs

  ClojureScript コンパイラ

- macchiato https://macchiato-framework.github.io/

  NodeJS の web server

- reitit https://github.com/metosin/reitit

  routing ライブラリ
