REGISTER 'naive-bayes.r' using com.cwoodson.pigaddons.rpig.RScriptEngine as rfuncs;

test_data0 = LOAD 'naive-bayes.data.test';

test_data1 = FOREACH test_data0 GENERATE $0 AS test_result:int, TOTUPLE($1 ..) AS fields;

test_spam = FILTER test_data1 BY test_result == 1;
test_spam1 = FOREACH test_spam GENERATE fields;
test_spam2 = GROUP test_spam1 ALL;
test_nospam = FILTER test_data1 BY test_result == 0;
test_nospam1 = FOREACH test_nospam GENERATE fields;
test_nospam2 = GROUP test_nospam1 ALL;

test_spam_cnt = FOREACH test_spam3 GENERATE SUM(test_spam1.$0), SUM(test_spam1.$1), SUM(test_spam1.$2), SUM(test_spam1.$3), SUM(test_spam1.$4), SUM(test_spam1.$5) , SUM(test_spam1.$6), SUM(test_spam1.$7), SUM(test_spam1.$8), SUM(test_spam1.$9), SUM(test_spam1.$10), SUM(test_spam1.$11), SUM(test_spam1.$12), SUM(test_spam1.$13), SUM(test_spam1.$14), SUM(test_spam1.$15), COUNT(test_spam);
test_nospam_cnt = FOREACH test_nospam2 GENERATE SUM(test_nospam1.$0), SUM(test_nospam1.$1), SUM(test_nospam1.$2), SUM(test_nospam1.$3), SUM(test_nospam1.$4), SUM(test_nospam1.$5), SUM(test_nospam1.$6), SUM(test_nospam1.$7), SUM(test_nospam1.$8), SUM(test_nospam1.$9), SUM(test_nospam1.$10), SUM(test_nospam1.$11), SUM(test_nospam1.$12), SUM(test_nospam1.$13), SUM(test_nospam1.$14), SUM(test_nospam1.$15), COUNT(test_nospam);

real_data = LOAD 'naive-bayes.data';

output = FOREACH real_data { fields = TOTUPLE($0 ..); } GENERATE rfuncs.CalcProb(fields, test_spam_cnt, test_nospam_cnt) as prob:(spam:int, pnotspam:double, pisspam:double);

STORE output INTO 'output/naive-bayes';