# BlackBerry&reg; Samples for Java

The BlackBerry&reg; Samples for Java project is an Open Source repository of samples that in the past were released through the BlackBerry&reg; Developer Knowledge Base or within the BlackBerry&reg; Java Tools.  The samples range in scope from Hello World, to Advanced UI components and include both samples and libraries that offer extended functionality.

The samples in the project have been re-formatted and adjusted for Open Source release.  Branches and Tags will be created to match the state of the samples as they were/are released along with a BlackBerry&reg; Java Development Environment.

The sample code is Open Source under the [Apache 2.0 License][].

  [Apache 2.0 License]: http://www.apache.org/licenses/LICENSE-2.0.html


**Applies To**

* [BlackBerry&reg; Java Development Environment](http://us.blackberry.com/developers/javaappdev/javadevenv.jsp)
* [BlackBerry&reg; Java Plug-in for Eclipse](http://us.blackberry.com/developers/javaappdev/javaupdate.jsp)


**Author** 

* Pete Vanderveen


**Dependencies**

1. [activation.jar][jaf-1.1.zip] which is part of [JavaBeans Activation Framework 1.1][jaf-1.1] is required for
   the Browser Multi-part Push Demo.  activation.jar should be extracted from the JavaBeans Activation Framework
   zip and placed in com/rim/samples/server/browsermultipartpushdemo/ as activation.jar.
2. [mail.jar][javamail-1.4.4.zip] which is part of [JavaMail API 1.4.4][javamail-1.4.4] is required for
   the Browser Multi-part Push Demo.  mail.jar should be extracted from the JavaMail API zip and placed in
   com/rim/samples/server/browsermultipartpushdemo/ as mail.jar.

  [jaf-1.1]: http://www.oracle.com/technetwork/java/jaf11-139815.html
  [jaf-1.1.zip]: http://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-java-plat-419418.html#jaf-1.1-fr-oth-JPR
  [javamail-1.4.4]: http://www.oracle.com/technetwork/java/javamail/index-138643.html
  [javamail-1.4.4.zip]: http://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-eeplat-419426.html#javamail-1.4.4-oth-JPR

* Everything required to compile and run the BlackBerry&reg; Samples for Java is included in either of the BlackBerry&reg; Java environments.

**To contribute code to this repository you must be [signed up as an official contributor](http://blackberry.github.com/howToContribute.html).**


## How to Build

To build the BlackBerry&reg; Samples:

With the BlackBerry&reg; JDE installed, load the BlackBerry&reg; JDE Samples for Java workspace and select **Build** from the **Build** menu or press the **F7** key.  All active projcets will compile.  You are notified of any compilation or syntax errors during compilation.  If any errors occur, the process stops.

**To activate or deavtivate a project**

Right-click on the project and select **Activate Project** or **Deactivate Project** or on the **Project** menu, click **Set Active Projects.**  Select the projects you want to activate.


## More Info

* [Java Application Development Overview][1]
* [Get started with the BlackBerry&reg; JDE][2]
* [Java Development Guides and API Reference][3]
* [BlackBerry&reg; Java Developer Support Forum][4]

  [1]: http://us.blackberry.com/developers/javaappdev/
  [2]: http://supportforums.blackberry.com/t5/Java-Development/Get-started-with-the-BlackBerry-JDE/ta-p/444837
  [3]: http://docs.blackberry.com/en/developers/subcategories/?userType=21&category=Java+Development+Guides+and+API+Reference
  [4]: http://supportforums.blackberry.com/t5/Java-Development/bd-p/java_dev


## Contributing Changes

To add new Samples or make modifications to existing Samples:

1. Fork the **BlackBerryJDE-Samples** repository
2. Make the changes/additions to your fork
3. Send a pull request from your fork back to the **BlackBerryJDE-Samples** repository
4. If you made changes to code which you are NOT the owner, send a message via github messages to the Author(s) of the Sample to indicate that you have a pull request for them to review
5. If you made changes to code which you own, send a message via github messages to one of the Committers listed below to have your code merged


## Bug Reporting and Feature Requests

If you find a bug in a Sample, or have an enhancement request, simply file an [Issue][] for the Sample and send a message (via github messages) to the Sample Author(s) to let them know that you have filed an [Issue][].

  [Issue]: https://github.com/blackberry/BlackBerryJDE-Samples/issues

## Disclaimer

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
