# SPA Guide Server

SPA チュートリアルで用いる API サーバです。

## 実行方法

System Requirements

- java (`brew install --cask adoptopenjdk`)
- node (> v16)

### DB マイグレーション + サンプルデータの注入

サーバを立ち上げる前に DB をセットアップする必要があります
DB をリセットしたくなったときにも同じコマンドを使ってください

```sh
rm -rf db.sqlite3
touch db.sqlite3
npm run migrate
npm run start_migrate
```

### API サーバ

まずはリリースパッケージにするためのコンパイルをしてください
(そこそこに時間がかかるので、事前に実行しておいてください)

```
npm run release
```

次に以下のコマンドでサーバを実行します

```
npm run start_release
```

Swager-UI は `http://127.0.0.1:3000/api-docs` にあります
