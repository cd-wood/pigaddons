register 'naive-bayes.r' using com.cwoodson.pigaddson.rpig.RScriptEngine as rfuncs;

test_data0 = LOAD 'naive-bayes.data.test';

test_data1 = FOREACH test_data0 GENERATE $0 AS test_result:int, TOTUPLE($1 ..) AS fields;

test_spam = FILTER test_data1 BY fields == 1;
test_spam1 = GROUP test_spam ALL;
test_nospam = FILTER test_data1 BY fields == 0;
test_nospam1 = GROUP test_nospam ALL;

test_spam_cnt = FOREACH test_spam1 GENERATE SUM(test_spam.$0), SUM(test_spam.$1), SUM(test_spam.$2), SUM(test_spam.$3), SUM(test_spam.$4), SUM(test_spam.$5) , SUM(test_spam.$6), SUM(test_spam.$7), SUM(test_spam.$8), SUM(test_spam.$9), SUM(test_spam.$10), SUM(test_spam.$11), SUM(test_spam.$12), SUM(test_spam.$13), SUM(test_spam.$14), SUM(test_spam.$15), COUNT(test_spam);
test_nospam_cnt = FOREACH test_nospam1 GENERATE SUM(test_nospam.$0), SUM(test_nospam.$1), SUM(test_nospam.$2), SUM(test_nospam.$3), SUM(test_nospam.$4), SUM(test_nospam.$5), SUM(test_nospam.$6), SUM(test_nospam.$7), SUM(test_nospam.$8), SUM(test_nospam.$9), SUM(test_nospam.$10), SUM(test_nospam.$11), SUM(test_nospam.$12), SUM(test_nospam.$13), SUM(test_nospam.$14), SUM(test_nospam.$15), COUNT(test_nospam);

real_data = LOAD 'naive-bayes.data';

output = FOREACH real_data { fields = TOTUPLE($0 ..); } GENERATE rfuncs.CalcProb(fields, test_spam_cnt, test_nospam_cnt) as prob:(spam:int, pnotspam:double, pisspam:double);

STORE output INTO 'output/naive-bayes';