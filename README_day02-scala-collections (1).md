# Day 2 — Scala Collections Practice

## Task
Practice `map`/`filter`/`flatMap`/`reduce` on a sales list, use a `Vector` for indexed customer records, a `Map` for product quantities/prices, a `for`-comprehension joining customers and orders, and produce a daily sales summary with `groupBy`.

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
  println(s"Customer at index 2: ${customers(2)}")
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

## Explanation — what's happening

**1. `map` / `filter` / `flatMap` / `reduce` on `sales`**
- `sales.map(_.amount)` pulls out just the `amount` field from each `Sale` — 1-to-1 transform.
- `sales.filter(_.amount > 20)` keeps only sales above 20 (Book and Bag qualify, Pen doesn't).
- `sales.flatMap(s => s.product.split(" "))` splits each product name into words and flattens all the resulting lists into one — here each product is a single word, so it just becomes a flat list of product names.
- `sales.map(_.amount).reduce(_ + _)` sums all amounts: 10.5+45.0+12.0+99.99 = 167.49.

**2. `Vector` for customers**
`customers(2)` reads index 2 directly — `Vector` gives near-constant-time random access (unlike `List`, which would have to walk 2 links). `.updated(1, "Bobby")` returns a **new** Vector with index 1 replaced — the original `customers` is untouched (immutability).

**3. `Map` for quantities and prices**
`qty.map { case (p, q) => p -> q * price.getOrElse(p, 0.0) }` iterates the `qty` map as `(key, value)` pairs and computes `quantity * price` for each product, using `getOrElse` so a missing price defaults to `0.0` instead of crashing.

**4. `for`-comprehension joining customers and orders**
```scala
for { o <- orders; if customers.contains(o.customer); p <- price.get(o.product) } yield ...
```
For each order, it checks the customer is a known customer, looks up the product's price (only proceeding if found — `price.get` returns an `Option`), and builds a summary string. This is really `orders.withFilter(...).flatMap(o => price.get(o.product).map(p => ...))` under the hood.

**5. `groupBy` for the daily summary**
`sales.groupBy(_.product)` buckets sales into a `Map[String, List[Sale]]` by product name, then `.map { case (p, l) => p -> l.map(_.amount).sum }` reduces each bucket to a total.

## Viva Q&A
| Question | Answer |
|---|---|
| Difference between `map` and `flatMap` here? | `map` transforms 1-to-1; `flatMap` on `s.product.split(" ")` would flatten multiple words per product into one list if any product name had spaces. |
| Why does `.updated(1, "Bobby")` not change the original `customers` Vector? | `Vector` is immutable — `updated` returns a brand-new Vector with structural sharing; the original reference is untouched. |
| What does `price.get(o.product)` return, and why use it instead of direct indexing? | It returns an `Option[Double]` — `Some(price)` if found, `None` if not — so a missing product silently skips that order in the `for`-comprehension instead of throwing. |
| What does `sales.groupBy(_.product)` return before the `.map` step? | A `Map[String, List[Sale]]` — each product mapped to its list of matching `Sale` records. |
| Is any of this running on Spark? | No — this is plain, eager Scala collections. Spark/RDDs start from Day 3 onward. |
