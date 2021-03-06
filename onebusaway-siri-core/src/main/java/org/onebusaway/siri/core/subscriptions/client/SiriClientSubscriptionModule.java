/**
 * Copyright (C) 2011 Brian Ferris <bdferris@onebusaway.org>
 * Copyright (C) 2011 Google, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onebusaway.siri.core.subscriptions.client;

import com.google.inject.AbstractModule;

public class SiriClientSubscriptionModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(SiriClientSubscriptionManager.class);
    bind(CheckStatusManager.class);
    bind(HeartbeatManager.class);
    bind(InitiateSubscriptionsManager.class);
    bind(TerminateSubscriptionsManager.class);
  }

  @Override
  public int hashCode() {
    return this.getClass().hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null)
      return false;
    return this.getClass().equals(o.getClass());
  }
}
