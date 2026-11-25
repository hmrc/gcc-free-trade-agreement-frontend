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

package forms

import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

class SearchFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "search.error.required"
  val invalidKey  = "search.error.invalid"

  val form = new SearchFormProvider()()

  ".value" - {

    val fieldName = "value"

    val validReferences: Gen[String] =
      Gen.listOfN(12, Gen.numChar).map(digits => "POD" + digits.mkString)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validReferences
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    "must not bind values in the wrong format" in {
      val invalidInputs = Seq(
        "ABC123",
        "POD12345678901",
        "POD1234567890123",
        "POD12345678901A",
        "123456789012",
        "XYZ123456789012",
        "POD123456789012!"
      )

      invalidInputs.foreach { input =>
        val result = form.bind(Map(fieldName -> input))(fieldName)
        result.errors.map(_.message) mustEqual Seq(invalidKey)
      }
    }

    "must accept valid references with unwanted characters and clean them up" in {
      val inputs = Seq(
        "POD123456789012 ",
        " POD123456789012",
        "POD 1234 5678 9012",
        "POD-123456789012",
        "POD123456789012\u00A0",
        "pod123456789012"
      )

      inputs.foreach { input =>
        val result = form.bind(Map("value" -> input))
        result.errors mustBe empty
        result.value.value mustEqual "POD123456789012"
      }
    }
  }
}
