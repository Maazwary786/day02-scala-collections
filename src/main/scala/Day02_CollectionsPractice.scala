object Day02_CollectionsPractice {

  case class Order(customerId: Int, product: String, qty: Int, price: Double)
  case class Customer(id: Int, name: String)

  def main(args: Array[String]): Unit = {
    val sales = List(
      Order(1, "Laptop", 2, 55000.0),
      Order(2, "Mouse", 5, 500.0),
      Order(1, "Keyboard", 1, 1500.0),
      Order(3, "Monitor", 2, 8000.0)
    )

    val totals = sales.map(o => o.qty * o.price)
    println(s"Order totals: $totals")

    val bigOrders = sales.filter(o => (o.qty * o.price) > 5000)
    println(s"Big orders: $bigOrders")

    val allProducts = sales.flatMap(o => List(o.product))
    println(s"All products (flatMap): $allProducts")

    val grandTotal = sales.map(o => o.qty * o.price).reduce(_ + _)
    println(s"Grand total sales: $grandTotal")

    val customers: Vector[Customer] = Vector(
      Customer(1, "Ravi"), Customer(2, "Anita"), Customer(3, "Zoya")
    )
    println(s"Customer at index 1 (fast lookup with Vector): ${customers(1)}")

    val productQty = sales.groupBy(_.product).view.mapValues(_.map(_.qty).sum).toMap
    println(s"Quantity per product: $productQty")

    val summary = for {
      c <- customers
      o <- sales if o.customerId == c.id
    } yield s"${c.name} bought ${o.qty} x ${o.product}"

    println("Daily Sales Summary:")
    summary.foreach(println)
  }
}
