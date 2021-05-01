package com.example.yudyang.regulus.core.sql.parser.component.like;

import com.example.yudyang.regulus.core.sql.model.AtomicQuery;

import static com.example.yudyang.regulus.core.antlr4.ElasticsearchParser.*;

public class DelegateLikeQueryParser {

    public AtomicQuery parse(LikeClauseContext likeClauseContext, int idx) {
        switch (idx){
            default:
            case FUZZY:
                return new PrefixLikeQueryParser().parse(likeClauseContext);
        }

    }
}
