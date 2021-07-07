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
import java.util.Date;

public interface ClockRepository {
  Timestamp toTimestamp();
  LocalDate toLocalDate();
  LocalDateTime toLocalDateTime();
  Date toDate(LocalDate localDate);
  Clock get();
  void set(Clock clock);
}
