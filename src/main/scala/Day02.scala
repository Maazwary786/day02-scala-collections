object Day02 extends App {

  case class Sale(product: String, amount: Double)
  val sales = List(Sale("Pen", 10.5), Sale("Book", 45.0), Sale("Pen", 12.0), Sale("Bag", 99.99))

  // map / filter / flatMap / reduce
  println(s"Amounts: ${sales.map(_.amount)}")
  println(s"Sales over 20: ${sales.filter(_.amount > 20)}")
  println(s"Words: ${sales.flatMap(s => s.product.split(" "))}")
  println(s"Total sales: ${sales.map(_.amount).reduce(_ + _)}")

  // Vector for indexed customer records
  val customers: Vector[String] = Vector("Alice", "Bob", "Charlie", "Diana")
  println(s"Customer at index 2: ${customers(2)}") // Vector gives fast O(log32 n) random access; List is O(n)
  println(s"Updated: ${customers.updated(1, "Bobby")}")

  // Map for quantities and prices
  val qty = Map("Pen" -> 100, "Book" -> 40, "Bag" -> 15)
  val price = Map("Pen" -> 10.5, "Book" -> 45.0, "Bag" -> 99.99)
  val inventoryValue = qty.map { case (p, q) => p -> q * price.getOrElse(p, 0.0) }
  println(s"Inventory value: $inventoryValue, total: ${inventoryValue.values.sum}")

  // for-comprehension combining customers and orders
  case class Order(customer: String, product: String, qty: Int)
  val orders = List(Order("Alice", "Pen", 5), Order("Bob", "Book", 2), Order("Charlie", "Bag", 1))

  val orderSummaries = for {
    o <- orders
    if customers.contains(o.customer)
    p <- price.get(o.product)
  } yield s"${o.customer} ordered ${o.qty} x ${o.product} = ${o.qty * p}"
  orderSummaries.foreach(println)

  // Scenario: daily sales summary
  val dailySummary = sales.groupBy(_.product).map { case (p, l) => p -> l.map(_.amount).sum }
  println(s"Daily sales summary: $dailySummary")
}
