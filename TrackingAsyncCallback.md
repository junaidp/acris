# Introduction #

### Related artifacts ###

```
<groupId>sk.seges.acris</groupId>
<artifactId>acris-callbacks</artifactId>
```

## Description ##

Asynchronous callback in GWT is the only way how can user/client be notified whenever the RPC call fails or succeed. Using callbacks is pretty straightforward, but there are some cases in real life (is the life of developer a real life ? :) ) when only AsyncCallback-s are not enought:
  * when you test your application using GWTTestCase and you want to be notified when any RPC request is finished in order to assert UI components or values in user interface generally
  * when there are chained asynchronous RPC calls (when first asynchronous call is finished, next one will start - request is started from onSuccess handling routine) and you want to be notified when asynchronous RPC calls chain is processed (first asynchronou call is considered as finished when also second one is finished)

Second scenario is described in next picture:

![http://acris.googlecode.com/svn/wiki/images/callbacks.png](http://acris.googlecode.com/svn/wiki/images/callbacks.png)

# Using TrackingAsyncCallback #

Let's consider that you have Panel which in init method loads data from database and displays in GUI. Now you want to test this panel automatically (using GwtTestCase), but:
  1. you have to set timeout in your test case using
```
delayTestFinish(timeout);
```
(note: if you don't know what I'm talking about, please see details here
http://code.google.com/intl/sk/webtoolkit/doc/latest/DevGuideTesting.html#DevGuideAsynchronousTesting )
The question is, what timeout will you define?
    * 10 seconds ? - what if any RPC call is still not finished?
    * 1 minute? - what if you have many testcases and each testcase will waste time in this way?

Now, when you are using TrackingAsyncCallback instead of original GWT AsyncCallback in your RPC calls, you can be notified when all RPC calls are finished in this way:
```
RPCRequestTracker.getTracker().registerCallbackListener(new ICallbackTrackingListener() {

	@Override
	public void onProcessingFinished(RPCRequest request) {
		if (request.getCallbackResult().equals(RequestState.REQUEST_FAILURE)) {
			fail("Unable to execute RPC calls. See the previous errors in console.", null);
		} else {
			if (request.getParentRequest() == null) {
				RPCRequestTracker.getTracker().removeAllCallbacks();
				finishTest(); 
			}
		}
	}

	@Override
	public void onRequestStarted(RPCRequest request) {
	}

	@Override
	public void onResponseReceived(RPCRequest request) {
	}
			
});
```

When onProcessingFinished method is called with request without any parentRequest, then all RPC requests are done (All request in chain are done and no new request was started)

Using TrackingAsyncCallback in RPC calls:
```
asyncService.method(new TrackingAsyncCallback<Void>() {

	public void onFailureCallback(Throwable caught) {
	}

	public void onSuccessCallback(Void result) {
	}
});
```
instead of original callback
```
asyncService.method(new AsyncCallback<Void>() {

	public void onFailure(Throwable caught) {
	}

	public void onSuccess(Void result) {
	}
});
```
... and thats all folks :)