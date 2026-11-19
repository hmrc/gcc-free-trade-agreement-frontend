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

import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.FtaRegistrationViewModel
import views.html.FtaRegistrationView

import scala.util.Random

class FtaRegistrationController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  view: FtaRegistrationView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = Action { implicit request =>
    val registrationDetails = FtaRegistrationViewModel(
      companyName = "Testdata Company Ltd",
      podNumber = s"POD${Iterator.fill(12)(Random.nextInt(10)).mkString}",
      status = "Registered",
      expiryFrom = "1 January 2026",
      expiryTo = "31 December 2026",
      history = "Registered on 1 January 2026"
    )

    Ok(view(registrationDetails))
  }
}
