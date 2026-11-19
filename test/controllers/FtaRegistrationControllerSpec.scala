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

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._

class FtaRegistrationControllerSpec extends SpecBase {

  "FtaRegistration Controller" - {
    val podRegex = """(?s).*POD[0-9]{12}.*"""

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder().build()

      running(application) {
        val request = FakeRequest(GET, routes.FtaRegistrationController.onPageLoad().url)

        val result = route(application, request).value

        val content = contentAsString(result)

        status(result) mustEqual OK
        content must include("govuk-summary-list")
        content must include("Check the account details")
        content must include("Testdata Company Ltd")
        content.matches(podRegex) mustEqual true
        content must include("Company Name")
        content must include("FTA Registration Status")
        content must include("Expiry From")
        content must include("Expiry To")
        content must include("FTA Registration History")
        content must include("Continue")
        content must include(s"""href="${routes.IndexController.onPageLoad().url}"""")
      }
    }
  }
}
