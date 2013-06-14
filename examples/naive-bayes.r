CalcProb <<- function(fields, spam_test_data, notspam_test_data) {
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

    CalcProb.make_tables <- function(n) {
        return(list(list(1 + num_notspam - notspam_test_data[[n]], 1 + notspam_test_data[[n]]), list(1 + num_spam - spam_test_data[[n]], 1 + spam_test_data[[n]])))
    }
    
    tables <- lapply(1:list_length, CalcProb.make_tables)

    Utils.logInfo(paste('tables: ', toString(tables)))

    CalcProb.make_probs <- function(n) {
        return(lapply(1:2, function(x) lapply(1:2, function(y) tables[[n]][[x]][[y]] / (tot + 4))))
    }
    
    probs <- lapply(1:list_length, CalcProb.make_probs)

    Utils.logInfo(paste('probs: ', toString(probs)))

    result <- list(isspam=0, pnotspam=0.0, pspam=0.0)

    CalcProb.inner <- function(n) {
        col <- fields[[n]] + 1
        result[[2]] <<- result[[2]] + log(probs[[n]][[1]][[col]])
        result[[3]] <<- result[[3]] + log(probs[[n]][[2]][[col]])
    }

    lapply(1:list_length, CalcProb.inner)
    
    result$pnotspam <- result$pnotspam - (log((num_notspam + 1) / tot) * (list_length - 1))
    result$pspam <- result$pspam - (log((num_spam + 1) / tot) * (list_length - 1))

    if(result$pspam > result$pnotspam) {
        result$isspam <- 1
    }
    return(result)
}
attributes(CalcProb)$outputSchema <- 'probabilities:(isspam:int, pnotspam:double, pspam:double)'