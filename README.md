## Intro

Burner exercise.

### How to Run

```
$cat test_script.sh 
AUTH_TOKEN='MA_gHUSYOlEAAAAAAAAADdIZPg8qV_LneCcqvNa7Gjr0phNT5b8aCKe91IA3C7ya'

sbt ";clean; run $AUTH_TOKEN"
$./test_script.sh
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

## Testing

### Testing Data in POSTMAN

Please import the POSTMAN [JSON samples](https://github.com/github/kevinmeredith/blob/master/postman_testing.json). 

It contains 5 HTTP Requests:

 1. MMS with link to URL for downloading Github Gist
 2. GET /report to view a map of MMS -> votes
 3. SMS voting on picture in Step 1's MMS
 4. SMS whose payload is not a vote on a picture
 5. Voicemail 

### Testing

Note - these tests will reference the test data POSTMAN requests in the above section:

 1. Run step 2 to see that the map is empty
 2. Run step 1 to get OK - a picture was uploaded
 3. Run step 2 to see that the map is empty (no one voted yet)
 4. Run step 3 to vote on the picture uploaded
 5. Run step 2 to see that there's a single map entry with a count of 1:

 	```json
 	{ "kevinmeredith/b28aff27e355e56dcde4/raw/c9136b177b931a49b865d1a6ac2ef2e5a645c291/test.txt": 1}
 	```
 6. Run step 5 to send a Voicemail
 7. Run the report (step 2) to verify that the map is unchanged
 8. Run Step 4 to send a non-voting SMS
 9. Run step 3 to vote on the picture 
 10. Run step 2 to see that there's a single map entry with a count of 2
 	```json
 	{ "kevinmeredith/b28aff27e355e56dcde4/raw/c9136b177b931a49b865d1a6ac2ef2e5a645c291/test.txt": 2}
 	```


## TODO's

If I were going to release this user story/feature to prod, I would prefer to, in general,
 add more types and tests where possible, as well as remove assumptions.

Namely, I would:

 * think about whether detach is needed - stackoverflow.com/questions/31364405/sprays-detach-directive
 * think about whether AtomicLongMap is OK, i.e. a shared, concurrent map
 * add unit and integration tests 
 * use Algebraic Data Types in the `DropboxService` return types - to avoid exceptions
 * Don't assume that, when uploading a file, that it successfully uploaded if its 'upload status' is
   Pending or Downloading - perform re-try logic.
 * Normalize URL paths to avoid a difference between /foo/bar.txt and /foo//bar.txt (I've gotten bitten by this before).
 * Come up with better way to name files, rather than use the entire relative path