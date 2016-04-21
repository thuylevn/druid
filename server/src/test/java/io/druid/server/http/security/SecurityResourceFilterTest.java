/*
 * Licensed to Metamarkets Group Inc. (Metamarkets) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Metamarkets licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.druid.server.http.security;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.sun.jersey.spi.container.ResourceFilter;
import io.druid.server.ClientInfoResource;
import io.druid.server.QueryResource;
import io.druid.server.StatusResource;
import io.druid.server.http.BrokerResource;
import io.druid.server.http.CoordinatorDynamicConfigsResource;
import io.druid.server.http.CoordinatorResource;
import io.druid.server.http.DatasourcesResource;
import io.druid.server.http.HistoricalResource;
import io.druid.server.http.IntervalsResource;
import io.druid.server.http.MetadataResource;
import io.druid.server.http.RulesResource;
import io.druid.server.http.ServersResource;
import io.druid.server.http.TiersResource;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.ws.rs.WebApplicationException;
import java.util.Collection;

@RunWith(Parameterized.class)
public class SecurityResourceFilterTest extends ResourceFilterTestHelper
{
  @Parameterized.Parameters
  public static Collection<Object[]> data()
  {
    return ImmutableList.copyOf(
        Iterables.concat(
            getRequestPaths(CoordinatorResource.class),
            getRequestPaths(DatasourcesResource.class),
            getRequestPaths(BrokerResource.class),
            getRequestPaths(HistoricalResource.class),
            getRequestPaths(IntervalsResource.class),
            getRequestPaths(MetadataResource.class),
            getRequestPaths(RulesResource.class),
            getRequestPaths(ServersResource.class),
            getRequestPaths(TiersResource.class),
            getRequestPaths(ClientInfoResource.class),
            getRequestPaths(CoordinatorDynamicConfigsResource.class),
            getRequestPaths(QueryResource.class),
            getRequestPaths(StatusResource.class)
        )
    );
  }

  private final String requestPath;
  private final String requestMethod;
  private final ResourceFilter resourceFilter;

  public SecurityResourceFilterTest(
      String requestPath,
      String requestMethod,
      ResourceFilter resourceFilter
  )
  {
    this.requestPath = requestPath;
    this.requestMethod = requestMethod;
    this.resourceFilter = resourceFilter;
  }

  @Before
  public void setUp() throws Exception
  {
    setUp(resourceFilter);
  }

  @Test
  public void testDatasourcesResourcesFilteringAccess()
  {
    setUpMockExpectations(requestPath, true, requestMethod);
    EasyMock.replay(req, request, authorizationInfo);
    resourceFilter.getRequestFilter().filter(request);
  }

  @Test(expected = WebApplicationException.class)
  public void testDatasourcesResourcesFilteringNoAccess()
  {
    setUpMockExpectations(requestPath, false, requestMethod);
    EasyMock.replay(req, request, authorizationInfo);
    resourceFilter.getRequestFilter().filter(request);
  }

  @After
  public void tearDown()
  {
    EasyMock.verify(req, request, authorizationInfo);
  }
}
