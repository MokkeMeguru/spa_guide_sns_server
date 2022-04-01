# 実装時に考えていたこと 3

## サーバとクライアント連携には何をするのが良いのか

サーバクライアント連携には OpenAPI のようなツールが重要であることがわかっている。
ところが実際はサーバがレスポンスを返してくれないとクライアントが開発できない、というような声を多く聞く。

理由として考えられているのは以下の 3 点

1. レスポンスに混じっているビジネスロジックがレスポンスとして欲しい
2. OpenAPI ベースのサーバモックが実用に耐えられない Toy でしかない
3. 新規の API を開発する、みたいなケースでは、既存の API を利用しつつ、みたいなことをしないといけないから難しい

そうなるとサーバ側がどう実装するべきかというという案としては以下の二点

1. Fast に Mock を提供できる簡単な サーバを建てる

   e.g. メモリ DB を用いる / dockerize するなどでインスタンスな環境としてクライアントにサーバアプリケーションを提供する
   クリーンアーキテクチャやっているんだからできないわけない…はず ()

2. とりあえず動く API 開発を気合と根性でやる

   まず動かしてそこからリファクタで固めていく感じ
   先に世界のすべてを見通してかっちりを作れる TDD とは相容れない感じもしつつ

インスタンスなサーバをざっくり建てられる環境ができれば、時間依存の実装や検証が簡単になるし、案外悪くはなさそう…？
問題点としては以下の通り

- サンプルデータをどのように調達するか
- 外部サーバと連携する場合に苦しい (外部のデータは手元のインスタンスサーバの環境のことを知らない)