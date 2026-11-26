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

package services

import javax.inject.{Inject, Singleton}
import models.{RegistrationValidityPeriod, VerifyStatusResponse}

import scala.util.Random

@Singleton
class VerifyStatusService @Inject() () {

  private val activePodPrefix   = "111111"
  private val inactivePodPrefix = "222222"

  def generatePodReference(prefix: String = "000000"): String = {
    val suffix = f"${Random.nextInt(1000000)}%06d"
    s"POD$prefix$suffix"
  }

  def generateActivePodReference(): String = generatePodReference(activePodPrefix)

  def generateInactivePodReference(): String = generatePodReference(inactivePodPrefix)

  def verifyStatus(podReference: String): VerifyStatusResponse =
    podReference match {
      case pod if pod.startsWith(s"POD$activePodPrefix") =>
        VerifyStatusResponse(
          matchFound = true,
          exporterCompanyName = Some("Example Exporter Ltd"),
          podReference = Some(pod),
          currentRegistrationStatus = Some("VALID"),
          registrationValidityPeriods = Seq(
            RegistrationValidityPeriod("VALID", "2026-01-01", Some("2026-04-24")),
            RegistrationValidityPeriod("NOT_VALID", "2026-06-01", Some("2026-08-31")),
            RegistrationValidityPeriod("VALID", "2026-09-01", Some("9999-12-31"))
          )
        )

      case pod if pod.startsWith(s"POD$inactivePodPrefix") =>
        VerifyStatusResponse(
          matchFound = true,
          exporterCompanyName = Some("Formerly Registered Example Exporter Ltd"),
          podReference = Some(pod),
          currentRegistrationStatus = Some("NOT_VALID"),
          registrationValidityPeriods = Seq(
            RegistrationValidityPeriod("VALID", "2024-01-01", Some("2025-03-31")),
            RegistrationValidityPeriod("NOT_VALID", "2025-04-01", Some("9999-12-31"))
          )
        )

      case pod =>
        VerifyStatusResponse(
          matchFound = false,
          matchNotFoundReason = Some(
            s"No authorisation was found with an authorisation reference number matching supplied Pod Reference $pod"
          )
        )
    }
}
