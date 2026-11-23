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
import services.VerifyStatusService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.FtaRegistrationViewModel
import views.html.FtaRegistrationView

class FtaRegistrationController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  view: FtaRegistrationView,
  verifyStatusService: VerifyStatusService
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(podReference: Option[String] = None): Action[AnyContent] = Action { implicit request =>
    val searchedPod     = podReference.getOrElse(verifyStatusService.generateActivePodReference())
    val response        = verifyStatusService.verifyStatus(searchedPod)
    val validityPeriods = response.registrationValidityPeriods.map { period =>
      viewmodels.RegistrationValidityPeriodViewModel(
        status = userFacingStatus(period.status),
        validFrom = period.validFrom,
        validTo = period.validTo.getOrElse("Not specified")
      )
    }

    val currentPeriod       = response.registrationValidityPeriods.lastOption
    val registrationDetails = FtaRegistrationViewModel(
      podNumber = searchedPod,
      matchFound = response.matchFound,
      noMatchReason = response.matchNotFoundReason,
      companyName = response.exporterCompanyName,
      status = response.currentRegistrationStatus.map(userFacingStatus),
      activeFrom = currentPeriod.map(_.validFrom),
      activeUntil = currentPeriod.flatMap(_.validTo),
      validityPeriods = validityPeriods
    )

    Ok(view(registrationDetails))
  }

  private def userFacingStatus(status: String): String =
    status match {
      case "VALID"     => "Active"
      case "NOT_VALID" => "Inactive"
      case unexpected  => throw new IllegalArgumentException(s"Unsupported registration status: $unexpected")
    }
}
