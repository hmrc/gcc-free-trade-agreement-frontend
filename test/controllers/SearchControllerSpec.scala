/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, MessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers.*

class SearchControllerSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite {

  private val url = routes.SearchController.onPageLoad().url

  "Search Controller" - {
    "must return OK for a GET" in {
      val result = route(app, FakeRequest(GET, url)).get
      status(result) mustEqual OK
    }

    "must return OK for a POST with a valid POD reference number" in {
      val request = FakeRequest(POST, url).withFormUrlEncodedBody("value" -> "POD123456789012")
      val result  = route(app, request).get
      status(result) mustEqual OK
    }

    "must return BAD_REQUEST for a POST with an empty term" in {
      val request = FakeRequest(POST, url).withFormUrlEncodedBody("value" -> "a" * 101)
      val result  = route(app, request).get
      status(result) mustEqual BAD_REQUEST
    }

    "must show the hint text above the search box on a GET" in {
      val messagesApi = app.injector.instanceOf[MessagesApi]
      val hintText    = messagesApi("search.hint")(Lang.defaultLang)

      val result  = route(app, FakeRequest(GET, url)).get
      val content = contentAsString(result)

      status(result) mustEqual OK
      content must include("id=\"value-hint\"")
      content must include(hintText)
    }

    "must return BAD_request and the format error for an incorrectly formatted term" in {
      val request = FakeRequest(POST, url).withFormUrlEncodedBody("value" -> "ABC123")
      val result  = route(app, request).get

      val messagesApi = app.injector.instanceOf[MessagesApi]
      val errorText   = messagesApi("search.error.invalid")(Lang.defaultLang)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) must include(
        errorText.replace("'", "&#x27;")
      )
    }
  }
}
