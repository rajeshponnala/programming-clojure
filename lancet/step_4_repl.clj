(ns lancet.step-4-repl
    (:use lancet.step-2-complete lancet.step-3-complete))

; START: has-run-fn
(defn has-run? [v]
  ((:has-run (meta v))))
; END: has-run-fn

(def has-run-fn has-run?)

; START: has-run-macro
(defmacro has-run? [f]
  `((:has-run (meta (var ~f)))))
; END: has-run-macro

; START: reset
(defmacro reset [f]
  `((:reset-fn (meta (var ~f)))))
; END: reset

; START: deftarget
(defmacro deftarget [sym doc & forms]
  (let [has-run (gensym "hr-") reset-fn (gensym "rf-")]
    `(let [[~has-run ~reset-fn once-fn#] (runonce (fn [] ~@forms))]
       (def ~(with-meta sym {:doc doc :has-run has-run :reset-fn reset-fn}) 
	    once-fn#))))
; END: deftarget

; START: define-ant-task
(defmacro define-ant-task [clj-name ant-name]
  `(defn ~clj-name [props#]
     (let [task# (instantiate-task ant-project ~(name ant-name) props#)]
       (.execute task#)
       task#)))
; END: define-ant-task

; START: task-names
(defn task-names [] (map symbol (seq (.. ant-project getTaskDefinitions keySet))))
; END: task-names

; START: safe-ant-name
(defn safe-ant-name [n]
  (if (ns-resolve 'clojure.core n) (symbol (str "ant-" n)) n))
; END: safe-ant-name

; START: define-all-ant-tasks
(defmacro define-all-ant-tasks []
  `(do ~@(map (fn [n] `(define-ant-task ~n ~n)) (task-names))))
; END: define-all-ant-tasks

; START: safe-define-all-ant-tasks
(defmacro define-all-ant-tasks []
  `(do ~@(map (fn [n] `(define-ant-task ~(safe-ant-name n) ~n)) (task-names))))
; END: safe-define-all-ant-tasks

(define-all-ant-tasks)


