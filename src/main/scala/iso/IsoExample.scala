package iso

import monocle.Iso

trait IsoExample {
  val MAGICAL_CONSTANT = 5.0 // this would be in pounds

  implicit val poundsEuroIso = Iso[Pounds, Euro](pounds  => Euro(pounds.amount * 0.89))(euro   => Pounds(euro.amount * 1.12))
  implicit val poundsLeiIso  = Iso[Pounds, Lei]  (pounds => Lei(pounds.amount * 5.19))(lei    => Pounds(lei.amount * 0.19))
  implicit val euroLeiIso    = Iso[Euro, Lei]    (pounds => Lei(pounds.amount * 4.64))(lei    => Euro(lei.amount * 0.22))
  implicit val pennyToPounds = Iso[Penny, Pounds](penny  => Pounds(penny.amount / 100))(pounds => Penny(pounds.amount * 100))//or whatever this is

  //and now that we've defined a penny to pounds Isomorphism, we automatically get a penny to anything else based on transition
  implicit def pennyToCurrency[C <: Currency](implicit iso1: Iso[Penny, Pounds], iso2: Iso[Pounds, C]): Iso[Penny, C] = {
    Iso[Penny, C]{penny => iso2.get(iso1.get(penny))}{other => iso1.reverseGet(iso2.reverseGet(other))}
  }

  // using Isomorphism to get and reverseGet a value to an universal one
  // we could go even further
  def applyTax[C <: Currency](amount: C)(implicit iso: Iso[Pounds, C]): C = {
    val poundsAmount = iso.reverseGet(amount)

    val amountToApply = Pounds(poundsAmount.amount * 32 / 100 - MAGICAL_CONSTANT)

    iso.get(amountToApply)
  }

  def transformPenny[C](penny: Penny)(implicit iso: Iso[Penny, C]) = {
    iso.get(penny)
  }

  def runDemo(): Unit = {
    println("Based on the relation of transition between Iso's, we'll try to convert Penny to Lei using Pound as in intermediary Iso")
    println(transformPenny[Lei](Penny(100)))
  }
}