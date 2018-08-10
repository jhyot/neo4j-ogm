/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with
 * separate copyright notices and license terms. Your use of the source
 * code for these subcomponents is subject to the terms and
 * conditions of the subcomponent's license, as noted in the LICENSE file.
 */

package org.neo4j.ogm.session.request.strategy.impl;

import static java.util.Objects.*;

import java.io.Serializable;
import java.util.Collection;

import org.neo4j.ogm.cypher.Filters;
import org.neo4j.ogm.cypher.query.PagingAndSortingQuery;
import org.neo4j.ogm.session.Utils;
import org.neo4j.ogm.session.request.FilteredQuery;
import org.neo4j.ogm.session.request.FilteredQueryBuilder;
import org.neo4j.ogm.session.request.strategy.LoadClauseBuilder;
import org.neo4j.ogm.session.request.strategy.MatchClauseBuilder;
import org.neo4j.ogm.session.request.strategy.QueryStatements;

/**
 * @author Vince Bickers
 * @author Luanne Misquitta
 * @author Mark Angrish
 * @author Nicolas Mervaillie
 * @author Michael J. Simons
 */
public class NodeQueryStatements<ID extends Serializable> implements QueryStatements<ID> {

    private String primaryIndex;

    private MatchClauseBuilder idMatchClauseBuilder = new IdMatchClauseBuilder();
    private MatchClauseBuilder idCollectionMatchClauseBuilder = new IdCollectionMatchClauseBuilder();
    private MatchClauseBuilder labelMatchClauseBuilder = new LabelMatchClauseBuilder();

    private LoadClauseBuilder loadClauseBuilder;

    public NodeQueryStatements(String primaryIndex, LoadClauseBuilder loadClauseBuilder) {
        this.primaryIndex = primaryIndex;
        this.loadClauseBuilder = requireNonNull(loadClauseBuilder);
    }

    @Override
    public PagingAndSortingQuery findOne(ID id, int depth) {
        return findOneByType("", id, depth);
    }

    @Override
    public PagingAndSortingQuery findOneByType(String label, ID id, int depth) {

        String matchClause;
        if (primaryIndex != null) {
            matchClause = idMatchClauseBuilder.build(label, primaryIndex);
        } else {
            matchClause = idMatchClauseBuilder.build(label);
        }
        String returnClause = loadClauseBuilder.build(label, depth);
        boolean returnsPath = depth != 0;
        return new PagingAndSortingQuery(matchClause, returnClause, Utils.map("id", id), returnsPath, false);
    }

    @Override
    public PagingAndSortingQuery findAllByType(String label, Collection<ID> ids, int depth) {

        String matchClause;
        if (primaryIndex != null) {
            matchClause = idCollectionMatchClauseBuilder.build(label, primaryIndex);
        } else {
            matchClause = idCollectionMatchClauseBuilder.build(label);
        }
        String returnClause = loadClauseBuilder.build(label, depth);
        boolean returnsPath = depth != 0;
        return new PagingAndSortingQuery(matchClause, returnClause, Utils.map("ids", ids), returnsPath, false);
    }

    @Override
    public PagingAndSortingQuery findByType(String label, int depth, boolean distinct) {

        String matchClause = labelMatchClauseBuilder.build(label, distinct);
        String returnClause = loadClauseBuilder.build(label, depth);
        final boolean returnsPath = depth != 0;
        return new PagingAndSortingQuery(matchClause, returnClause, Utils.map(), returnsPath, false);
    }

    @Override
    public PagingAndSortingQuery findByType(String label, Filters parameters, int depth, boolean distinct) {

        FilteredQuery filteredQuery = FilteredQueryBuilder.buildNodeQuery(label, parameters);
        String matchClause = filteredQuery.statement();
        String returnClause = loadClauseBuilder.build(label, depth);
        boolean returnsPath = depth != 0;
        return new PagingAndSortingQuery(matchClause, returnClause, filteredQuery.parameters(), returnsPath, true);
    }

}
