(ns domain.mock-test
  (:require [cljs.test :as t]
            [clojure.spec.alpha :as s]
            [domain.mock :as sut]
            [domain.user]
            [domain.community]
            [domain.community.member]
            [domain.community.event]
            [domain.community.event.comment]))

(t/deftest test-samples
  (t/testing "user"
    (t/is (s/valid? (s/* ::domain.user/command) (:user sut/samples))))
  (t/testing "community"
    (t/is (s/valid? (s/* ::domain.community/command) (:community sut/samples))))
  (t/testing "member"
    (t/is (s/valid? (s/* ::domain.community.member/command) (:community-member sut/samples))))
  (t/testing "event"
    (t/is (s/valid? (s/* ::domain.community.event/command) (:community-event sut/samples))))
  (t/testing "comment"
    (t/is (s/valid? (s/* ::domain.community.event.comment/command) (:community-event-comment sut/samples)))))
