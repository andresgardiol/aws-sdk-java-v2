/*
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.extensions.dynamodb.mappingclient;

import java.util.concurrent.CompletableFuture;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * Interface for a single operation that can be executed against a mapped database table. These operations will be
 * executed against the primary index of the table. Conceptually an operation maps 1:1 with an actual DynamoDb call.
 * <p>
 * A concrete implementation of this interface should also implement {@link IndexOperation} with the same types if
 * the operation supports being executed against both the primary index and secondary indices.
 *
 * @param <ItemT> The modelled object that this table maps records to.
 * @param <RequestT>  The type of the request object for the DynamoDb call in the low level {@link DynamoDbClient}.
 * @param <ResponseT> The type of the response object for the DynamoDb call in the low level {@link DynamoDbClient}.
 * @param <ResultT> The type of the mapped result object that will be returned by the execution of this operation.
 */
@SdkPublicApi
public interface TableOperation<ItemT, RequestT, ResponseT, ResultT>
    extends CommonOperation<ItemT, RequestT, ResponseT, ResultT> {
    /**
     * Default implementation of a complete synchronous execution of this operation against the primary index. It will
     * construct a context based on the given table name and then call execute() on the {@link CommonOperation} interface to
     * perform the operation.
     *
     * @param tableSchema A {@link TableSchema} that maps the table to a modelled object.
     * @param tableName The physical name of the table to execute the operation against.
     * @param dynamoDbClient A {@link DynamoDbClient} to make the call against.
     * @param mapperExtension A {@link MapperExtension} that may modify the request or result of this operation. A
     *                        null value here will result in no modifications.
     * @return A high level result object as specified by the implementation of this operation.
     */
    default ResultT executeOnPrimaryIndex(TableSchema<ItemT> tableSchema,
                                          String tableName,
                                          MapperExtension mapperExtension,
                                          DynamoDbClient dynamoDbClient) {
        OperationContext context = OperationContext.of(tableName, TableMetadata.primaryIndexName());
        return execute(tableSchema, context, mapperExtension, dynamoDbClient);
    }

    /**
     * Default implementation of a complete non-blocking asynchronous execution of this operation against the primary
     * index. It will construct a context based on the given table name and then call executeAsync() on the
     * {@link CommonOperation} interface to perform the operation.
     *
     * @param tableSchema A {@link TableSchema} that maps the table to a modelled object.
     * @param tableName The physical name of the table to execute the operation against.
     * @param dynamoDbAsyncClient A {@link DynamoDbAsyncClient} to make the call against.
     * @param mapperExtension A {@link MapperExtension} that may modify the request or result of this operation. A
     *                        null value here will result in no modifications.
     * @return A {@link CompletableFuture} of the high level result object as specified by the implementation of this
     * operation.
     */
    default CompletableFuture<ResultT> executeOnPrimaryIndexAsync(TableSchema<ItemT> tableSchema,
                                                                  String tableName,
                                                                  MapperExtension mapperExtension,
                                                                  DynamoDbAsyncClient dynamoDbAsyncClient) {

        OperationContext context = OperationContext.of(tableName, TableMetadata.primaryIndexName());
        return executeAsync(tableSchema, context, mapperExtension, dynamoDbAsyncClient);
    }
}
