##
# SPA Guide (SNS Server)
#
# @file
# @version 0.1

.PHONY: clear
clear:
	rm -f src.md5

build-check: src
	npm run release
	npm run migrate
	npm run openapi
	npm run start_openapi

build-all: clear build-check
	find src -type f -exec md5sum {} \; | awk '{ print $$2 "\t" $$1 }' | sort > src.md5

.PHONY: debug-migrate
debug-migrate: src
	rm -rf db.sqlite3
	touch db.sqlite3
	npm run migrate
	npm run start_migrate

.PHONY: debug-release
debug-release: src
	npm run release
	npm run start_release

.PHONY: debug-openapi
debug-openapi: src
	npm run openapi
	npm run start_openapi
# end
