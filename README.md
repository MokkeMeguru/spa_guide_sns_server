# SPA Guide Server

SPA チュートリアルで用いる API サーバです。

## 実行方法

System Requirements

- node (> v16)

System Requirements (with build)

- java (`brew install --cask adoptopenjdk`)

### DB マイグレーション + サンプルデータの注入

サーバを立ち上げる前に DB をセットアップする必要があります
DB をリセットしたくなったときにも同じコマンドを使ってください

```sh
npm install --production
rm -rf db.sqlite3
touch db.sqlite3
npm run start_migrate
```

### API サーバ

以下のコマンドでサーバを実行します

```
npm install --production
npm run start_release
```

Swager-UI は `http://127.0.0.1:3000/api-docs` にあります

## Openapi.yaml の生成

```
npm install --production
npm run start_openapi
```

### ソースのビルド

ソースのビルドには次のコマンドを使います

```
npm install
npm run migrate
npm run release
npm run openapi
```
