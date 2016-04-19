# Mustangs not in the stable #

Executing multiple asynchronous calls is like stampedo - releasing mustangs into the wild - you never know when and if they come back. In case of an asynchronous call there is a probability based on your server-side development skill that it will return no exception.

But what to do if your logic depends on finishing multiple not-directly-dependent asynchronous calls? You have to wait for each and every call and process it only when all are back (either with success or failure).

### Related artifacts ###

```
<groupId>sk.seges.acris</groupId>
<artifactId>acris-client-core</artifactId>
```

# AcrIS semaphore #

With a semaphore you can simply say that you are waiting for your 3 wild horses and only Manitou will force you to stop waiting.

```
// monitor two states
final Semaphore semaphore = new Semaphore(2);
```

With the semaphore instantiated above you are waiting for a service (or anything else) with two states - in our example it will be successful state or failure state.

```
semaphore.raise(4);
```

**Raising** the semaphore with count of 4 it means you are expecting four services to return in a state. It doesn't matter if success or failure, you are waiting for the moment when they are finished.

And when that happens you will carefully listen:

```
semaphore.addListener(new SemaphoreListener() {
			@Override
			public void change(SemaphoreEvent event) {
				if (event.getCount() == event.getStates()[0] + event.getStates()[1]) {
					// do something
					UserDialog.show("Everything was fine");
				}
			}});
```

Now you just need to **signal** the semaphore every time your service returns a state:

```
userService.login(token, new TrackingAsyncCallback<ClientSession>() {
				@Override
				public void onFailureCallback(Throwable cause) {
					// here we signal second state of the semaphore
					semaphore.signal(1);
				}

				@Override
				public void onSuccessCallback(ClientSession result) {
					// first state is the successful one
					semaphore.signal(0);
				}
			});
```

# Example #

You can find showcase for using semaphores and dynamic GUI notification in [acris-client-core-showcase](http://acris.googlecode.com/svn/trunk/acris-client-core-showcase/)