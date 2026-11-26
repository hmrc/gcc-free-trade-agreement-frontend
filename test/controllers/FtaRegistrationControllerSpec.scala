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
import services.VerifyStatusService
import play.api.test.Helpers._

class FtaRegistrationControllerSpec extends SpecBase {

  "FtaRegistration Controller" - {
    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder().build()

      running(application) {
        val verifyStatusService = application.injector.instanceOf[VerifyStatusService]
        val podReference        = verifyStatusService.generateActivePodReference()
        val request             = FakeRequest(GET, routes.FtaRegistrationController.onPageLoad(Some(podReference)).url)

        val result = route(application, request).value

        val content = contentAsString(result)

        status(result) mustEqual OK
        content must include("govuk-summary-list")
        content must include(s"Trader details for $podReference")
        content must include("Example Exporter Ltd")
        content must include(podReference)
        content must include("UK trader exporter company name")
        content must include("Registration status")
        content must include("Registration active from")
        content must include("Registration active until")
        content must include("Active")
        content must include("Registration History")
        content must include("2026-01-01")
        content must include("2026-04-24")
        content must include("2026-09-01")
        content must include("Continue")
        content must include(s"""href="${routes.IndexController.onPageLoad().url}"""")
      }
    }

    "must display the searched POD and inactive status for a previously registered trader" in {
      val application = applicationBuilder().build()

      running(application) {
        val verifyStatusService = application.injector.instanceOf[VerifyStatusService]
        val podReference        = verifyStatusService.generateInactivePodReference()
        val request             = FakeRequest(
          GET,
          routes.FtaRegistrationController.onPageLoad(Some(podReference)).url
        )
        val result              = route(application, request).value
        val content             = contentAsString(result)

        status(result) mustEqual OK
        content must include("Formerly Registered Example Exporter Ltd")
        content must include(podReference)
        content must include("Inactive")

        val summaryListStart = content.indexOf("<dl class=\"govuk-summary-list\">")
        val summaryListEnd   = content.indexOf("</dl>", summaryListStart) + "</dl>".length
        val summaryList      = content.substring(summaryListStart, summaryListEnd)

        summaryList must not include "Registration active from"
        summaryList must not include "Registration active until"
        content     must include("2024-01-01")
        content     must include("2025-04-01")
      }
    }

    "must display a no-match response for an unknown POD" in {
      val application = applicationBuilder().build()

      running(application) {
        val verifyStatusService = application.injector.instanceOf[VerifyStatusService]
        val searchedPod         = verifyStatusService.generatePodReference("333333")
        val request             = FakeRequest(GET, routes.FtaRegistrationController.onPageLoad(Some(searchedPod)).url)
        val result              = route(application, request).value
        val content             = contentAsString(result)

        status(result) mustEqual OK
        content must include(searchedPod)
        content must include("No authorisation was found")
      }
    }
  }
}
