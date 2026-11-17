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
  val lengthKey = "search.error.length"
  val invalidKey = "search.error.invalid"
  val maxLength = 100

  val form = new SearchFormProvider()()

  ".value" - {

    val fieldName = "value"
    
    val validStrings: Gen[String] =
      Gen.alphaNumStr.suchThat(s => s.nonEmpty && s.length <= maxLength)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validStrings
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
    
    "must not bind strings containing non-alphanumeric characters" in {
      val invalidInputs = Seq("abc!", "<script>", "£100", "a_b", "a-b")
      invalidInputs.foreach { input =>
        val result = form.bind(Map(fieldName -> input)).apply(fieldName)
        result.errors must contain only FormError(fieldName, invalidKey, Seq("^[A-Za-z0-9 ]+$"))
      }
    }
  }
}
