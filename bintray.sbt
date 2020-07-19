ThisBuild / publishTo := Some("sloth-apimodule bintray" at "https://api.bintray.com/maven/naftoligug/maven/sloth-apimodule")

ThisBuild / credentials ++=
  sys.env.get("BINTRAYKEY").toSeq.map(Credentials("Bintray API Realm", "api.bintray.com", "naftoligug", _))
