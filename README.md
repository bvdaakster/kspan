# KSpan, the Spannable Builder.
```java
/**
 * KSpan, the Spannable Builder.
 *
 * Creates a [Spannable] by joining the segments of [stringSegments] and applying the styles applied
 *  using this builder.
 *
 * Set [insertSpaces] to `true` to insert spaces when joining the segments.
 */
 ```
 
 Example:
 ```kotlin
sampleTextView.text = kspan(R.array.kspan_sample) {
  foregroundColor(R.color.colorPrimary, 1, 3)
  backgroundColor(R.color.colorAccent, 4)
  sampleTextView.click(0) {
    Toast.makeText(this@MainActivity, "Hello", Toast.LENGTH_SHORT).show()
  }
}
 ```
