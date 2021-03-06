-- The MIT License (MIT)

-- Copyright (c) 2013 Connor Woodson

-- Permission is hereby granted, free of charge, to any person obtaining a copy
-- of this software and associated documentation files (the "Software"), to deal
-- in the Software without restriction, including without limitation the rights
-- to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
-- copies of the Software, and to permit persons to whom the Software is
-- furnished to do so, subject to the following conditions:
--
-- The above copyright notice and this permission notice shall be included in
-- all copies or substantial portions of the Software.
--
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
-- IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
-- FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
-- AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
-- LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
-- OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
-- IN THE SOFTWARE.

REGISTER '$JARS/pigaddons-0.1-SNAPSHOT.jar';
REGISTER '$SCRIPTS/naive-bayes.r' USING com.cwoodson.pigaddons.rpig.RScriptEngine AS rfuncs;

test_data = LOAD '$INPUT/naive-bayes.data.training' AS (test_result:int, a:int, b:int, c:int, d:int, e:int, f:int, g:int, h:int, i:int, j:int, k:int, l:int, m:int, n:int, o:int, p:int);

SPLIT test_data INTO test_spam IF test_result == 1, test_nospam IF test_result == 0;

test_spam1 = GROUP test_spam ALL;
test_nospam1 = GROUP test_nospam ALL;

test_spam_cnt = FOREACH test_spam1 GENERATE TOTUPLE(SUM(test_spam.a), SUM(test_spam.b), SUM(test_spam.c), SUM(test_spam.d), SUM(test_spam.e), SUM(test_spam.f) , SUM(test_spam.g), SUM(test_spam.h), SUM(test_spam.i), SUM(test_spam.j), SUM(test_spam.k), SUM(test_spam.l), SUM(test_spam.m), SUM(test_spam.n), SUM(test_spam.o), SUM(test_spam.p), COUNT(test_spam)) AS spam_test_data, 'xx' AS join_key;
test_notspam_cnt = FOREACH test_nospam1 GENERATE TOTUPLE(SUM(test_nospam.a), SUM(test_nospam.b), SUM(test_nospam.c), SUM(test_nospam.d), SUM(test_nospam.e), SUM(test_nospam.f), SUM(test_nospam.g), SUM(test_nospam.h), SUM(test_nospam.i), SUM(test_nospam.j), SUM(test_nospam.k), SUM(test_nospam.l), SUM(test_nospam.m), SUM(test_nospam.n), SUM(test_nospam.o), SUM(test_nospam.p), COUNT(test_nospam)) AS notspam_test_data, 'xx' AS join_key;

real_data = LOAD '$INPUT/naive-bayes.data' AS (a:int, b:int, c:int, d:int, e:int, f:int, g:int, h:int, i:int, j:int, k:int, l:int, m:int, n:int, o:int, p:int);

real_data_aux = FOREACH real_data GENERATE TOTUPLE($0 ..) AS fields, 'xx' AS join_key;

test_data_joined = JOIN test_spam_cnt BY join_key, test_notspam_cnt BY join_key;

real_data_joined = JOIN real_data_aux BY join_key LEFT, test_data_joined BY test_spam_cnt::join_key;

result = FOREACH real_data_joined { flds = real_data_aux::fields; spam = test_spam_cnt::spam_test_data; notspam = test_notspam_cnt::notspam_test_data; result = rfuncs.CalcProb(flds, spam, notspam); GENERATE result; }

STORE result INTO '$OUTPUT/naive-bayes';