package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form

class SearchPageFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form(
      "value" -> text("searchPage.error.required")
        .verifying(maxLength(100, "searchPage.error.length"))
    )
}
