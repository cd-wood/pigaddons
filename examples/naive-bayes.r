# The MIT License (MIT)
#
# Copyright (c) 2013 Connor Woodson
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
# IN THE SOFTWARE.

CalcProb <<- function(fields, spam_test_data, notspam_test_data) {
    # Start with variable error checking
    # To make sure Pig invoked the function correctly
    if(!is.list(fields)) {
        Utils.logError('Param 1 to CalcProb is not a list')
        return(NULL)
    }
    if(!is.list(spam_test_data)) {
        Utils.logError('Param 2 to CalcProb is not a list')
        return(NULL)
    }
    if(!is.list(notspam_test_data)) {
        Utils.logError('Param 3 to CalcProb is not a list')
        return(NULL)
    }
    list_length <- length(fields);
    if(list_length <= 0) {
        Utils.logError('Param 1 to CalcProb has length <= 0')
        return(NULL)
    }
    if(length(spam_test_data) != list_length + 1) {
        Utils.logError('Param 2 to CalcProb has incorrect length')
        return(NULL)
    }
    if(length(notspam_test_data) != list_length + 1) {
        Utils.logError('Param 3 to CalcProb has incorrect length')
        return(NULL)
    }

    num_spam <- spam_test_data[[list_length + 1]]
    num_notspam <- notspam_test_data[[list_length + 1]]
    tot <- num_spam + num_notspam

    # Makes a Naive Bayes table for each field representing the following:
    #
    #  |  0  |  1  |
    # -|-----|-----|
    # 0|  a  |  b  |
    # -|-----|-----|
    # 1|  c  |  d  |
    # -|-----|-----|
    #
    # a: Number of times variable was 0 and event was 0 (not spam)
    # b: Number of times variable was 1 and event was 0
    # c: Number of times variable was 0 and event was 1 (spam)
    # d: Number of times variable was 1 and event was 1
    #
    # Note that notspam_test_data contains the values for b
    # and spam_test_data contains the values for d
    # We add 1 in all cases because of LaPlace
    CalcProb.make_tables <- function(n) {
        return(list(list(1 + num_notspam - notspam_test_data[[n]], 1 + notspam_test_data[[n]]), list(1 + num_spam - spam_test_data[[n]], 1 + spam_test_data[[n]])))
    }
    
    tables <- lapply(1:list_length, CalcProb.make_tables)

    # Calculate the probability, divide each cell by total number of events
    # +4 is because of LaPlace
    # It is possible to combine the two operations into one function, but that
    # simplification has been left out to show the separate steps
    CalcProb.make_probs <- function(n) {
        return(lapply(1:2, function(x) lapply(1:2, function(y) tables[[n]][[x]][[y]] / (tot + 4))))
    }
    
    probs <- lapply(1:list_length, CalcProb.make_probs)

    # Our resulting list must have names.
    # The names are more important than the order, as the
    # final order is determined by the output schema
    result <- list(isspam=0, pnotspam=0.0, pspam=0.0)

    # First we assume our result is 0 (not spam) and add the probability of
    # the current variable in that instance.
    # Then we repeat with the assumption that our result is 1
    CalcProb.inner <- function(n) {
        col <- fields[[n]] + 1
        result$pnotspam <<- result$pnotspam + log(probs[[n]][[1]][[col]])
        result$pspam <<- result$pspam + log(probs[[n]][[2]][[col]])
    }

    lapply(1:list_length, CalcProb.inner)
    
    # Do a final adjustment to finish off our conditional probability equations
    result$pnotspam <- result$pnotspam - (log((num_notspam + 1) / tot) * (list_length - 1))
    result$pspam <- result$pspam - (log((num_spam + 1) / tot) * (list_length - 1))

    if(result$pspam > result$pnotspam) {
        result$isspam <- 1
    }
    return(result)
}
attributes(CalcProb)$outputSchema <- 'probabilities:(isspam:int, pnotspam:double, pspam:double)'