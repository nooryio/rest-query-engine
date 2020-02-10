/*
 *  com.github.rutledgepaulv.rqe.pipes.MultiValueFieldsWithNestedObjectsTest
 *  *
 *  * Copyright (C) 2016 Paul Rutledge <paul.v.rutledge@gmail.com>
 *  *
 *  * This software may be modified and distributed under the terms
 *  * of the MIT license.  See the LICENSE file for details.
 *
 */

package com.github.rutledgepaulv.rqe.pipes;

import com.github.rutledgepaulv.qbuilders.builders.GeneralQueryBuilder;
import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import com.github.rutledgepaulv.rqe.testsupport.Comment;
import com.github.rutledgepaulv.rqe.testsupport.User;
import org.junit.Ignore;
import org.junit.Test;

public class MultiValueFieldsWithNestedObjectsTest extends TestBase {

    static {
        MODE = RunMode.TEST;
    }

    private QueryConversionPipeline pipeline = QueryConversionPipeline.defaultPipeline();


    @Test
    public void propertiesOnObjectsInMultiValueField_notSubquery() {

        Condition<GeneralQueryBuilder> condition = pipeline.apply("comments.comment=='This is my first comment'", User.class);

        assertMongo(condition, "{\"comments.comment\": \"This is my first comment\"}");

        assertElasticsearch(condition, "{\n" +
            "  \"term\" : {\n" +
            "    \"comments.comment\" : {\n" +
            "      \"value\" : \"This is my first comment\",\n" +
            "      \"boost\" : 1.0\n" +
            "    }\n" +
            "  }\n" +
            "}");

    }

    @Test
    public void propertiesOnObjectsInMultiValueField_predicate_notSubquery() {

        Condition<GeneralQueryBuilder> condition = pipeline.apply("comments.comment=='This is my first comment'", User.class);

        User user = new User();
        Comment comment1 = new Comment();
        comment1.setComment("This is my first comment");

        Comment comment2 = new Comment();
        comment2.setComment("This is my second comment");

        user.getComments().add(comment1);
        user.getComments().add(comment2);

        assertPredicate(condition, user);

        comment1.setComment("Something else");
        assertNotPredicate(condition, user);

    }


    @Test
    @Ignore // TODO: add again when "q-builders" is fixed for latest ElasticSearch
    public void nestedQuery() {

        Condition<GeneralQueryBuilder> condition = pipeline.apply("comments=q=\"comment=='This is my first comment';timestamp=ex=true\"", User.class);

        assertMongo(condition, "{\"comments\": \"{\\\"$elemMatch\\\": \\\"{\\\\\\\"$and\\\\\\\": [{\\\\\\\"comment\\\\\\\": \\\\\\\"This is my first comment\\\\\\\"}, {\\\\\\\"timestamp\\\\\\\": {\\\\\\\"$exists\\\\\\\": true}}]}\\\"}\"}");

        assertElasticsearch(condition, "{\n" +
                "  \"nested\" : {\n" +
                "    \"query\" : {\n" +
                "      \"bool\" : {\n" +
                "        \"must\" : [ {\n" +
                "          \"term\" : {\n" +
                "            \"comments.comment\" : \"This is my first comment\"\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"exists\" : {\n" +
                "            \"field\" : \"comments.timestamp\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    },\n" +
                "    \"path\" : \"comments\"\n" +
                "  }\n" +
                "}");

    }

}
