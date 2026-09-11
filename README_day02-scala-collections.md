# Day 2 — Scala Collections Practice

Covers: `map`/`filter`/`flatMap`/`reduce` on a sales list, `Vector` for indexed customer records, `Map` for product quantities/prices, a `for`-comprehension joining customers and orders, and a daily sales summary via `groupBy`.

## How to Run
```bash
sbt run
```

## Code — `src/main/scala/Day02.scala`
```scala
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
```

## Output
```
Amounts: List(10.5, 45.0, 12.0, 99.99)
Sales over 20: List(Sale(Book,45.0), Sale(Bag,99.99))
Words: List(Pen, Book, Pen, Bag)
Total sales: 167.49
Customer at index 2: Charlie
Updated: Vector(Alice, Bobby, Charlie, Diana)
Inventory value: Map(Pen -> 1050.0, Book -> 1800.0, Bag -> 1499.85), total: 4349.85
Alice ordered 5 x Pen = 52.5
Bob ordered 2 x Book = 90.0
Charlie ordered 1 x Bag = 99.99
Daily sales summary: Map(Pen -> 22.5, Book -> 45.0, Bag -> 99.99)
```

### Notes
- These are plain (eager) Scala collection operations — no Spark involved yet.
- `reduce` requires at least one element (it uses the first element as the initial accumulator); an empty list would throw.
- `groupBy(_.product)` returns a `Map[String, List[Sale]]`, which is then reduced to totals per product.
