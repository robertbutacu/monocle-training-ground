package iso

import monocle.Iso

trait IsoExample {
  val MAGICAL_CONSTANT = 5.0 // this would be in pounds

  implicit val poundsEuroIso: Iso[Pounds, Euro]  = Iso[Pounds, Euro](pounds  => Euro(pounds.amount * 1.12))(euro   => Pounds(euro.amount * 0.89))
  implicit val poundsLeiIso : Iso[Pounds, Lei]   = Iso[Pounds, Lei]  (pounds => Lei(pounds.amount * 5.19))(lei     => Pounds(lei.amount * 0.19))
  implicit val pennyToPounds: Iso[Penny, Pounds] = Iso[Penny, Pounds](penny  => Pounds(penny.amount / 100))(pounds => Penny(pounds.amount * 100))//or whatever this is

  implicit val euroToLeiIsoTransition: Iso[Euro, Lei] = poundsEuroIso.reverse.composeIso(poundsLeiIso)
  implicit val euroLeiIsoDirect      : Iso[Euro, Lei] = Iso[Euro, Lei](euro => Lei(euro.amount * 4.64))(lei => Euro(lei.amount * 0.22))

  def poundToCurrency[C <: Currency](implicit iso: Iso[Pounds, C]): Iso[Pounds, C] = iso

  //and now that we've defined a penny to pounds Isomorphism, we automatically get a penny to anything else based on transition
  implicit def pennyToCurrency[C <: Currency](implicit iso: Iso[Pounds, C]): Iso[Penny, C] = pennyToPounds.composeIso(poundToCurrency[C])


  // using Isomorphism to get and reverseGet a value to an universal one
  // we could go even further
  def applyTax[C <: Currency](amount: C)(implicit iso: Iso[Pounds, C]): C = {
    val poundsAmount = iso.reverseGet(amount)

    val amountToApply = Pounds(poundsAmount.amount * 32 / 100 - MAGICAL_CONSTANT)

    iso.get(amountToApply)
  }

  def transformPenny[C](penny: Penny)(implicit iso: Iso[Penny, C]): C = iso.get(penny)
  def transformToLei(euro: Euro)(iso: Iso[Euro, Lei]): Lei   = iso.get(euro)

  def runDemo(): Unit = {
    println("Based on the relation of transition between Iso's, we'll try to convert Penny to Lei and then Euro using Pound as in intermediary Iso")
    println(transformPenny[Lei](Penny(100)))
    println(transformPenny[Euro](Penny(100)))
    println("\n\n\n")

    println("Comparing the direct iso with the one obtained through transition")
    println(transformToLei(Euro(100))(euroLeiIsoDirect))
    println(transformToLei(Euro(100))(euroToLeiIsoTransition))
  }
}