(ns infrastructure.sqlite3.up)

(def users-table "
CREATE TABLE IF NOT EXISTS users(
id TEXT primary key,
name TEXT UNIQUE,
icon_url TEXT,
created_at INT,
updated_at INT);")

(def communities-table "
CREATE TABLE IF NOT EXISTS communities(
id TEXT primary key,
name TEXT UNIQUE,
details TEXT,
category INT,
created_at INT,
updated_at INT);")

(def community-members-table "
CREATE TABLE IF NOT EXISTS community_members(
id TEXT primary key,
community_id TEXT,
user_id TEXT,
role INT,
created_at INT,
updated_at INT,
UNIQUE(community_id, user_id),
FOREIGN KEY (community_id) REFERENCES communities(id),
FOREIGN KEY (user_id) REFERENCES users(id));")

(def community-events-table "
CREATE TABLE IF NOT EXISTS community_events(
id TEXT primary key,
community_id TEXT,
owned_member_id TEXT,
name TEXT UNIQUE,
details TEXT,
hold_at TEXT,
created_at INT,
updated_at INT,
FOREIGN KEY (community_id) REFERENCES communities(id),
FOREIGN KEY (owned_member_id) REFERENCES community_members(id));")

(def community-event-comments-table "
CREATE TABLE IF NOT EXISTS community_event_comments(
id TEXT primary key,
event_id TEXT,
member_id TEXT,
body TEXT,
created_at INT,
updated_at INT,
FOREIGN KEY (event_id) REFERENCES community_events(id),
FOREIGN KEY (member_id) REFERENCES community_members(id));")
