(ns infrastructure.sqlite3.up)

(def users-table "
CREATE TABLE IF NOT EXISTS users(
id TEXT NOT NULL primary key,
name TEXT NOT NULL UNIQUE,
icon_url TEXT NOT NULL,
created_at INT NOT NULL,
updated_at INT NOT NULL);")

(def communities-table "
CREATE TABLE IF NOT EXISTS communities(
id TEXT NOT NULL primary key,
name TEXT NOT NULL UNIQUE,
details TEXT NOT NULL,
category INT NOT NULL,
created_at INT NOT NULL,
updated_at INT NOT NULL);")

(def community-members-table "
CREATE TABLE IF NOT EXISTS community_members(
id TEXT NOT NULL primary key,
community_id TEXT NOT NULL,
user_id TEXT NOT NULL,
role INT NOT NULL ,
created_at INT NOT NULL,
updated_at INT NOT NULL,
UNIQUE(community_id, user_id),
FOREIGN KEY (community_id) REFERENCES communities(id),
FOREIGN KEY (user_id) REFERENCES users(id));")

(def community-events-table "
CREATE TABLE IF NOT EXISTS community_events(
id TEXT NOT NULL primary key,
community_id TEXT NOT NULL,
owned_member_id TEXT NOT NULL,
name TEXT NOT NULL UNIQUE,
details TEXT NOT NULL,
hold_at INT NOT NULL,
created_at INT NOT NULL,
updated_at INT NOT NULL,
FOREIGN KEY (community_id) REFERENCES communities(id),
FOREIGN KEY (owned_member_id) REFERENCES community_members(id));")

(def community-event-comments-table "
CREATE TABLE IF NOT EXISTS community_event_comments(
id TEXT NOT NULL primary key,
event_id TEXT NOT NULL,
member_id TEXT NOT NULL,
body TEXT NOT NULL,
comment_at INT NOT NULL,
created_at INT NOT NULL,
updated_at INT NOT NULL,
FOREIGN KEY (event_id) REFERENCES community_events(id),
FOREIGN KEY (member_id) REFERENCES community_members(id));")
