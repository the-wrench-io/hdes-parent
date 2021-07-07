package io.resys.wrench.assets.bundle.spi;

/*-
 * #%L
 * wrench-component-assets-integrations
 * %%
 * Copyright (C) 2016 - 2017 Copyright 2016 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.Service;
import io.resys.wrench.assets.bundle.api.repositories.AssetServiceRepository.ServiceType;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AssetIntegrationTest.ServiceTestConfig.class})
public class AssetIntegrationTest {

  @Autowired
  private AssetServiceRepository assetServiceRepository;
  @Autowired
  private ApplicationContext context;

  @Configuration
  public static class ServiceTestConfig {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
  }

  @Test
  public void services() {
    List<Service> services = assetServiceRepository.createQuery().list();
    Assert.assertEquals(6, services.size());
  }

  @Test
  public void dt() throws IOException {
    Service dt = assetServiceRepository.createQuery().dt("test decision table");
    Assert.assertEquals(getContent("assets/dt/testDt.json"), dt.getSrc());
    Assert.assertEquals("testDt.json", dt.getPointer());
    Assert.assertEquals("test decision table", dt.getName());
    Assert.assertEquals(ServiceType.DT, dt.getType());
  }

  @Test
  public void flow() throws IOException {
    Service flow = assetServiceRepository.createQuery().flow("evaluateRating");
    Assert.assertEquals(getContent("assets/flow/evaluateRating.json"), flow.getSrc());
    Assert.assertEquals("evaluateRating.json", flow.getPointer());
    Assert.assertEquals("evaluateRating", flow.getName());
    Assert.assertEquals(ServiceType.FLOW, flow.getType());
  }


  @Test
  public void flowTasks() {

    Service task = assetServiceRepository.createQuery().flowTask("RuleGroup1");
    Assert.assertNotNull(task.getSrc());
    Assert.assertEquals("ruleGroup1.json", task.getPointer());
    Assert.assertEquals("RuleGroup1", task.getName());
    Assert.assertEquals(ServiceType.FLOW_TASK, task.getType());

    task = assetServiceRepository.createQuery().flowTask("RuleGroup2");
    Assert.assertNotNull(task.getSrc());
    Assert.assertEquals("ruleGroup2.json", task.getPointer());
    Assert.assertEquals("RuleGroup2", task.getName());
    Assert.assertEquals(ServiceType.FLOW_TASK, task.getType());
  }

  public String getContent(String location) throws IOException {
    return IOUtils.toString(context.getResource("classpath:" + location).getInputStream(), Charset.forName("utf-8"));
  }
}
