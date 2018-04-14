package models

import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, number, tuple,email}

/**
  * Created by vinay on 4/5/17.
  */
object FormsData {

  val userForm = Form(
    tuple(
      "email" -> email,
      "password" -> nonEmptyText(minLength = 6,maxLength = 10)
    )
  )

  val createUserForm = Form(
    mapping(
      "name" -> nonEmptyText(maxLength = 15),
      "age" -> number(min = 18, max = 99),
      "email" -> nonEmptyText,
      "password" -> nonEmptyText(minLength = 6,maxLength = 10)
    )(UserFormData.apply)(UserFormData.unapply)
  )
}
