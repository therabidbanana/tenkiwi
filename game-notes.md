# tenkiwi


## Next Steps


### The Debrief

Debrief structure:

1. intro / dossier
2. On the Case
  (Vote)
3. Getting In
  (Vote)
4. Getting Out
  (Vote)
5. Final thoughts


* Final scoring presentation
  - Should always try to move Agent Pickles to the middle via last minute vote
    changes from Pickles' perspective (as simulated by the software). If put in
    first place, Agent Pickles will not be "promoted", and removed from final
    score.
* allow multiple settings
  - fix character display (name / vs codename / etc)
* Character "gen"
  - Allow updating names
  - what if Paranoia? (Let users pick features for each other)
* Better looking scoreboard

### General

Bugfixes:

* Maybe websocket disconnect that was undetected (I refreshed) - could we make
  sure a heartbeat is going and at least shows disconnect status?
* Log exceptions (right now dropped silently)

Code cleanup:

* Reset any current user info on refresh (name)
* next-player often gets overridden or stored in gamestate - better naming needed
* Use vars on buttons / card text + next-player in game state -> rather than
  having to rewrite the actions / card text on pass
* Cleaner action text
* Start game duplication of setup logic
* Easier way to distinguish outgoing socket events and incoming events
  (some naming convention? Maybe all outgoing events are ->foo... shared event?)

Optimizations / Quality of Life

* 1 second updates of entire gamestate are wasteful. Make gamestate smaller/have partial updates
* Click effect - color change / something?
* Turn notification
* Allow game length configuration
* Manual leave
  (boot should soft-ban?)
* Only host can actually boot
* All players state ready
* Start game - host only ability
* Cleaner Home Screen
* Don't broadcast deck / discard?
* Per-player displays, with personal broadcast, instead of "active/inactive"
* Clean up memory

New Features

* Leave game support - needs to rewrite state
* User login (itch based?)

## Development

Open a terminal and type `lein repl` to start a Clojure REPL
(interactive prompt).

In the REPL, type

```clojure
(go)
(cljs-repl)
```

The call to `(go)` starts the Figwheel server at port 3449, which takes care of
live reloading ClojureScript code and CSS, and the app server at port 10555
which forwards requests to the http-handler you define.

Running `(cljs-repl)` starts the Figwheel ClojureScript REPL. Evaluating
expressions here will only work once you've loaded the page, so the browser can
connect to Figwheel.

When you see the line `Successfully compiled "resources/public/app.js" in 21.36
seconds.`, you're ready to go. Browse to `http://localhost:10555` and enjoy.

**Attention: It is not needed to run `lein figwheel` separately. Instead `(go)`
launches Figwheel directly from the REPL**

## Trying it out

If all is well you now have a browser window saying 'Hello Chestnut',
and a REPL prompt that looks like `cljs.user=>`.

Open `resources/public/css/style.css` and change some styling of the
H1 element. Notice how it's updated instantly in the browser.

Open `src/cljs/tenkiwi/core.cljs`, and change `dom/h1` to
`dom/h2`. As soon as you save the file, your browser is updated.

In the REPL, type

```
(ns tenkiwi.core)
(swap! app-state assoc :text "Interactivity FTW")
```

Notice again how the browser updates.

### Lighttable

Lighttable provides a tighter integration for live coding with an inline
browser-tab. Rather than evaluating cljs on the command line with the Figwheel
REPL, you can evaluate code and preview pages inside Lighttable.

Steps: After running `(go)`, open a browser tab in Lighttable. Open a cljs file
from within a project, go to the end of an s-expression and hit Cmd-ENT.
Lighttable will ask you which client to connect. Click 'Connect a client' and
select 'Browser'. Browse to [http://localhost:10555](http://localhost:10555)

View LT's console to see a Chrome js console.

Hereafter, you can save a file and see changes or evaluate cljs code (without
saving a file).

### Emacs/CIDER

CIDER is able to start both a Clojure and a ClojureScript REPL simultaneously,
so you can interact both with the browser, and with the server. The command to
do this is `M-x cider-jack-in-clojurescript`.

We need to tell CIDER how to start a browser-connected Figwheel REPL though,
otherwise it will use a JavaScript engine provided by the JVM, and you won't be
able to interact with your running app.

Put this in your Emacs configuration (`~/.emacs.d/init.el` or `~/.emacs`)

``` emacs-lisp
(setq cider-cljs-lein-repl
      "(do (user/go)
           (user/cljs-repl))")
```

Now `M-x cider-jack-in-clojurescript` (shortcut: `C-c M-J`, that's a capital
"J", so `Meta-Shift-j`), point your browser at `http://localhost:10555`, and
you're good to go.

## Testing

To run the Clojure tests, use

``` shell
lein test
```

To run the Clojurescript you use [doo](https://github.com/bensu/doo). This can
run your tests against a variety of JavaScript implementations, but in the
browser and "headless". For example, to test with PhantomJS, use

``` shell
lein doo phantom
```

## Deploying to Heroku

This assumes you have a
[Heroku account](https://signup.heroku.com/dc), have installed the
[Heroku toolbelt](https://toolbelt.heroku.com/), and have done a
`heroku login` before.

``` sh
git init
git add -A
git commit
heroku create
git push heroku master:master
heroku open
```

## Running with Foreman

Heroku uses [Foreman](http://ddollar.github.io/foreman/) to run your
app, which uses the `Procfile` in your repository to figure out which
server command to run. Heroku also compiles and runs your code with a
Leiningen "production" profile, instead of "dev". To locally simulate
what Heroku does you can do:

``` sh
lein with-profile -dev,+production uberjar && foreman start
```

Now your app is running at
[http://localhost:5000](http://localhost:5000) in production mode.

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

## Chestnut

Created with [Chestnut](http://plexus.github.io/chestnut/) 0.18.0 (40a06fcf).
