Failure detection model and membership using central heart beating.
====================
Every process sends a heartbeat saying "I am alive" to a pre-defined leader. The leader declares a process Pi as having failed if it doesn't receive Pi's heartbeat within a timeout.

This is a partially-synchronous system; all processes run in a single computer and hence have bounds on communication channel and process speeds(to some extent). This becomes an asynchronous system if move each process into its own computer.

We can simulate both these properties, variable message speed and processor speeds.

Process failures: crash-stop. A process stops executing any instructions when it crashes.
