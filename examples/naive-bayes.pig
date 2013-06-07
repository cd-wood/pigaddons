REGISTER 'naive-bayes.r' using com.cwoodson.pigaddons.rpig.RScriptEngine as rfuncs;

test_data = LOAD 'naive-bayes.data.test' AS (test_result:int, a:int, b:int, c:int, d:int, e:int, f:int, g:int, h:int, i:int, j:int, k:int, l:int, m:int, n:int, o:int, p:int);

test_spam = FILTER test_data BY test_result == 1;
test_spam1 = GROUP test_spam ALL;
test_nospam = FILTER test_data BY test_result == 0;
test_nospam1 = GROUP test_nospam ALL;

test_spam_cnt = FOREACH test_spam1 GENERATE SUM(test_spam.a), SUM(test_spam.b), SUM(test_spam.c), SUM(test_spam.d), SUM(test_spam.e), SUM(test_spam.f) , SUM(test_spam.g), SUM(test_spam.h), SUM(test_spam.i), SUM(test_spam.j), SUM(test_spam.k), SUM(test_spam.l), SUM(test_spam.m), SUM(test_spam.n), SUM(test_spam.o), SUM(test_spam.p), COUNT(test_spam);
test_nospam_cnt = FOREACH test_nospam1 GENERATE SUM(test_nospam.a), SUM(test_nospam.b), SUM(test_nospam.c), SUM(test_nospam.d), SUM(test_nospam.e), SUM(test_nospam.f), SUM(test_nospam.g), SUM(test_nospam.h), SUM(test_nospam.i), SUM(test_nospam.j), SUM(test_nospam.k), SUM(test_nospam.l), SUM(test_nospam.m), SUM(test_nospam.n), SUM(test_nospam.o), SUM(test_nospam.p), COUNT(test_nospam);

real_data = LOAD 'naive-bayes.data' AS (a:int, b:int, c:int, d:int, e:int, f:int, g:int, h:int, i:int, j:int, k:int, l:int, m:int, n:int, o:int, p:int);

output = FOREACH real_data { fields = TOTUPLE($0 ..); GENERATE rfuncs.CalcProb(fields, test_spam_cnt, test_nospam_cnt) as prob:(spam:int, pnotspam:double, pisspam:double); }

STORE output INTO 'output/naive-bayes';