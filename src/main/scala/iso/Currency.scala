package iso

trait Currency {
  def amount: Double
}

case class Pounds(amount: Double) extends Currency
case class Penny(amount: Double)  extends Currency
case class Euro(amount: Double)   extends Currency
case class Lei(amount: Double)    extends Currency
