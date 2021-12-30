package io.resys.wrench.assets.bundle.spi.clock;

/*-
 * #%L
 * wrench-assets-services
 * %%
 * Copyright (C) 2016 - 2019 Copyright 2016 ReSys OÜ
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

import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import io.resys.hdes.client.spi.util.HdesAssert;

public class SystemClockRepository implements ClockRepository {
  private Clock clock;

  public SystemClockRepository() {
      super();
      this.clock = Clock.systemUTC();
    }

  @Override
  public Clock get() {
    return clock;
  }

  @Override
  public void set(Clock clock) {
    HdesAssert.notNull(clock, () -> "clock can not be null!");
    this.clock = clock;
  }

  @Override
  public LocalDateTime toLocalDateTime() {
    return LocalDateTime.now(clock);
  }

  @Override
  public Timestamp toTimestamp() {
    return Timestamp.valueOf(LocalDateTime.now(clock));
  }

  @Override
  public LocalDate toLocalDate() {
    return LocalDateTime.now(clock).toLocalDate();
  }

  @Override
  public Date toDate(LocalDate localDate) {
    HdesAssert.notNull(localDate, () -> "localDate can not be null!");
    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }
}
