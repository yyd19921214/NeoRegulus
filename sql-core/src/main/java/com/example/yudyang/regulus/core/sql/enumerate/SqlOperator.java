package com.example.yudyang.regulus.core.sql.enumerate;

public enum SqlOperator {
    Equality,
    NotEqual,
    ApproximatelyEqual,
    ApproximatelyNotEqual,
    MatchPhrase,
    NotMatchPhrase,
    MatchPhrasePrefix,
    NotMatchPhrasePrefix,
    Not,
    GreaterThan,
    GreaterThanOrEqual,
    LessThan,
    LessThanOrEqual,
    IsNull,
    IsNotNull,
    In,
    NotIn,
    BetweenAnd,
    Like,
    NotLike
}
