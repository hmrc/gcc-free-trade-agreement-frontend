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

import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, MessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.VerifyStatusService
import forms.SearchFormProvider
import views.html.SearchView

class SearchControllerSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite {

  private val url = "/gcc-free-trade-agreement-frontend/search-pod-number"

  "Search Controller" - {
    "must return OK for a GET" in {
      val result = route(app, FakeRequest(GET, url)).get
      status(result) mustEqual OK
    }

    "must redirect to the FTA registration page with a valid POD number" in {
      val verifyStatusService = app.injector.instanceOf[VerifyStatusService]
      val podReference        = verifyStatusService.generateActivePodReference()
      val request             = FakeRequest(POST, url).withFormUrlEncodedBody("value" -> podReference)
      val result              = route(app, request).get
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(routes.FtaRegistrationController.onPageLoad(Some(podReference)).url)
    }

    "must return BAD_REQUEST for a POST with an empty term" in {
      val request = FakeRequest(POST, url).withFormUrlEncodedBody("value" -> "")
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

    "must return BAD_REQUEST and the format error for an incorrectly formatted term" in {
      val request = FakeRequest(POST, url).withFormUrlEncodedBody("value" -> "ABC123")
      val result  = route(app, request).get

      val messagesApi = app.injector.instanceOf[MessagesApi]
      val errorText   = messagesApi("search.error.invalid")(Lang.defaultLang)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) must include(
        errorText.replace("'", "&#x27;")
      )
    }

    "must show the heading and Search button on a GET" in {
      val messagesApi = app.injector.instanceOf[MessagesApi]
      val content     = contentAsString(route(app, FakeRequest(GET, url)).get)

      content must include(messagesApi("search.heading")(Lang.defaultLang))
      content must include(messagesApi("search.button")(Lang.defaultLang))
    }

    "must show the wrong format error in the title, summary and field" in {
      val messagesApi = app.injector.instanceOf[MessagesApi]
      val request     = FakeRequest(POST, url).withFormUrlEncodedBody("value" -> "ABC123")
      val content     = contentAsString(route(app, request).get)

      content must include("Error: " + messagesApi("search.title")(Lang.defaultLang))
      content must include("There is a problem")
      content must include(messagesApi("search.error.invalid")(Lang.defaultLang))
    }

    "must accept a reference with spaces and redirect with the cleaned value" in {
      val request = FakeRequest(POST, url).withFormUrlEncodedBody("value" -> "POD 1234 5678 9012 ")
      val result  = route(app, request).get

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(
        routes.FtaRegistrationController.onPageLoad(Some("POD123456789012")).url
      )
    }

    "must display the superuser link when the user is a superuser" in {
      val searchView   = app.injector.instanceOf[SearchView]
      val formProvider = app.injector.instanceOf[SearchFormProvider]
      val messagesApi  = app.injector.instanceOf[MessagesApi]
      val request      = FakeRequest()

      val content = searchView(
        formProvider(),
        None,
        true
      )(request, messagesApi.preferred(request)).toString

      content must include("Manage user enrolments")
    }

    "must not display the superuser link when the user is not a superuser" in {
      val searchView   = app.injector.instanceOf[SearchView]
      val formProvider = app.injector.instanceOf[SearchFormProvider]
      val messagesApi  = app.injector.instanceOf[MessagesApi]
      val request      = FakeRequest()

      val content = searchView(
        formProvider(),
        None,
        false
      )(request, messagesApi.preferred(request)).toString

      content must not include "Manage user enrolments"
    }

    "must display the superuser link with a placeholder destination" in {
      val result = route(app, FakeRequest(GET, url)).value

      status(result) mustBe OK
      contentAsString(result) must include("""href="#" """.trim)
    }
  }
}
