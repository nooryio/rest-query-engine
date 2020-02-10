/*
 *  com.github.rutledgepaulv.rqe.testsupport.CriteriaSerializer
 *  *
 *  * Copyright (C) 2016 Paul Rutledge <paul.v.rutledge@gmail.com>
 *  *
 *  * This software may be modified and distributed under the terms
 *  * of the MIT license.  See the LICENSE file for details.
 *
 */

package com.github.rutledgepaulv.rqe.testsupport;

import com.mongodb.DBObject;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Spring doesn't know how to serialize enums on a toString call
 */
public class CriteriaSerializer implements Function<Criteria, String> {

    @Override
    public String apply(Criteria criteria) {
        return applyInternal(criteria.getCriteriaObject());
    }

    private String applyInternal(DBObject object) {
        object.keySet().stream().forEach(key ->
            object.put(key, object.get(key) instanceof Enum<?> ?
                object.get(key).toString() : object.get(key) instanceof DBObject ?
                applyInternal((DBObject) object.get(key)) : object.get(key) instanceof Collection<?> ?
                applyList((Collection<?>)object.get(key)) : object.get(key)));

        return object.toString();
    }

    private String applyInternal(Document object) {
        object.keySet().stream().forEach(key ->
            object.put(key, object.get(key) instanceof Enum<?> ?
                object.get(key).toString() : object.get(key) instanceof Document ?
                applyInternal((Document) object.get(key)) : object.get(key) instanceof Collection<?> ?
                applyList((Collection<?>)object.get(key)) : object.get(key)));

        return object.toJson();
    }

    private List<?> applyList(Collection<?> items) {
        return items.stream().map(item -> {
            if (item instanceof Enum<?>) {
                return Objects.toString(item);
            } else {
                return item;
            }}).collect(Collectors.toList());
    }
}
