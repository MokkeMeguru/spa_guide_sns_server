##
# SPA Guide (SNS Server)
#
# @file
# @version 0.1

.PHONY: clear
clear:
	rm -f src.md5

build-all: src clear
	npm run release
	npm run migrate
	npm run openapi
	npm run start_openapi
	find src -type f -exec md5sum {} \; | awk '{ print $$2 "\t" $$1 }' | sort > src.md5

.PHONY: debug-migrate
debug-migrate: src
	rm -rf db.sqlite3
	touch db.sqlite3
	npm run migrate
	npm run start_migrate
# end
