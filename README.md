## Intro

TODO

### How to Run

```
>sbt clean run
```

## Routes:

* `POST /events`

* `GET /report`

## 3 Type of Messages

```
'{ "type": "inboundText", "payload": "Hello", "fromNumber": "+12222222222", "toNumber": "+13333333333" }'
'{ "type": "inboundMedia", "payload": "<picture url>", "fromNumber": "+12222222222", "toNumber": "+ 13333333333" }'
'{ "type": "voiceMail", "payload": "<voice mail url>", "fromNumber": "+12222222222", "toNumber": "+ 13333333333" }'
```

## Initial Testing

 1. `GET http://localhost:9000/report` returns `{}`
 2. `POST http://localhost:9000/event` with below body returns OK

	```json
	{ "type": "inboundMedia", "payload": "Hello", "fromNumber": "+12222222222", "toNumber": "+13333333333" }
	```
 3. `GET http://localhost:9000/report` returns `{"Hello": 1}`
 4. Re-send step 2 to get OK
 5. `GET http://localhost:9000/report` returns `{"Hello": 2}`

## TODO:

 * think about whether detach is needed - stackoverflow.com/questions/31364405/sprays-detach-directive
 * think about whether AtomicLongMap is OK for this job (might be replaced when using dropbox)
 * add drop-box service impl
 * add docs & maybe tests
 * add section on - what else I'd add if this were going to production