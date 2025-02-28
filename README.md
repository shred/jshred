# jshred ![build status](https://shredzone.org/badge/jshred.svg) ![maven central](https://shredzone.org/maven-central/org.shredzone.jshred/jshred/badge.svg)

*jshred* is a Java library containing classes I am frequently using. Maybe some are also useful for you?

*jshred* is mainly a collection of Swing classes I wrote in the last couple of years. Since Swing seems to be almost dead, there will be only very minor changes in this library in the future.

Note that `jshred-spring` has been moved to `commons-taglib` now. Please do not use `jshred-spring` on new projects and move old projects to `commons-tablib`.

This software is part of the Shredzone Commons.

## Features

* Sortable JTable
* `HTTPRequest` with file upload support
* Miscellaneous Swing helpers
* `JLabelGroup` for simple form layouts
* `JSortedTable` for sorted tables
* JPanel with background gradients
* JImageViewer to view images
* CSVLine to read and write CSV lines
* A very simple `XMLWriter`
* A confirm dialog which can remeber the last decision
* A Swing component for print previews
* A dialog headline component
* and more...

## Documentation

See the [online documentation](https://shredzone.org/maven/jshred/).

## Requirements

_jshred_ requires at least JDK 1.7.

* *R19* was the last release that required at least JDK 1.6.
* *R17* was the last release that required at least JDK 1.5.
* *R14* was the last release that required at least JDK 1.4.
* *R12* was the last release that required at least JDK 1.2 (some parts JDK 1.3).

If you are updating from R14 or earlier, note that R15 brought some changes in the API that are not downward compatible. Deprecated methods have been removed, and other methods got a new signature to reflect the new generics and enum features. *It is highly recommended that you recompile your software and check it for compile errors when you update from R14 or earlier.* If you just replace the jar file, your program will likely break at runtime.

## Contribute

* Fork the [Source code at Codeberg](https://codeberg.org/shred/jshred). Feel free to send pull requests.
* Found a bug? [File a bug report!](https://codeberg.org/shred/jshred/issues)

## License

_jshred_ is released under [GNU General Public License Version 3](http://www.gnu.org/licenses/gpl-3.0.html), [GNU Lesser General Public License Version 3](http://www.gnu.org/licenses/lgpl-3.0.html), [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt). It is free software. You may also use _jshred_ in a commercial software free of charge, without needing to buy a commercial license or release your source code.
