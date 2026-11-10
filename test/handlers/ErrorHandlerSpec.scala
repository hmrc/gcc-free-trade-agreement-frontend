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

package handlers

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ErrorTemplate

class ErrorHandlerSpec extends SpecBase {

  "ErrorHandler" - {

    "must render the standard error template" in {

      val application = applicationBuilder().build()

      running(application) {
        implicit val request: FakeRequest[?] = FakeRequest()

        val handler = application.injector.instanceOf[ErrorHandler]
        val view    = application.injector.instanceOf[ErrorTemplate]

        val result = handler.standardErrorTemplate("title", "heading", "message").futureValue

        result.toString mustEqual view("title", "heading", "message")(request, messages(application)).toString
      }
    }

    "must return NOT_FOUND with the error page for an unknown route" in {

      val application = applicationBuilder().build()

      running(application) {
        val request = FakeRequest(GET, "/gcc-free-trade-agreement-frontend/does-not-exist")

        val result = route(application, request).value

        status(result) mustEqual NOT_FOUND
        contentAsString(result) must include("govuk-heading-xl")
      }
    }
  }
}
