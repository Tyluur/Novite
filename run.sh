java -Xmx1024m -Xss2m -Dsun.java2d.noddraw=true -XX:+DisableExplicitGC -XX:+AggressiveOpts -XX:+UseAdaptiveGCBoundary -XX:MaxGCPauseMillis=500 -XX:SurvivorRatio=16 -XX:+UseParallelGC -classpath bin:data/lib/* novite.Main