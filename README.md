# Executive Summary

Generally speaking, an information system (IS) represents the state of another, target system (TS) by storing data, and contains algorithms that allows interaction with this data by reading or controlled modification of that state. To enhance this picture, we can separate the data we use to represent that state (SI), and the structure of that data: what object types have what attributes, what are the possible values of those attributes - this is the "meta information" (MI). We generally create and test those algorithms against the known meta information, meaning that our algorithms are free from the SI they handle, but tied to the MI. They can only handle information in the given structure (do not "see" anything that was not coded) and becomes unreliable when value assumptions are broken (famous quote from Chernobyl).

We start information systems by creating source codes and configurations to describe the MI, our current understanding of the TS's internal structure because we can only start implementing algorithms after this step. However, in most cases our understanding changes during the development and after deployment, we must follow changes in the TS. We have created many design and implementation methodologies over decades to deal with this dynamism, with varying success.

A general knowledge management platform is a special category as by definition, it does not know much about the MI of the TS that will use it. Broadly speaking such platforms are the programming languages (with their compilers, interpreters or runtimes), database managers or spreadsheet editors. They offer "meta-meta" structures (classes/members, tables/columns, sheets/tables) into which you must transform the MI of your target system and you are good to go. However, they all are still vulnerable to the changes in the MI, the more algorithms they contain, the less flexible they are.

There is another approach to knowledge management that starts from a TS, separates the MI of the static from the mutable segments and create a standard by describing how to store the mutable meta and state information, requiring an undefined IS to handle them. An excellent example is the XBRL standard that relies on de-facto general knowledge storage mechanisms (XML, XSD, XLink, ...) both for storing facts of actual business reports and to describe the taxonomies that the reports use to identify those facts.

# milab-dust

The milab-dust project is a limited, pragmatic implementation of a broader knowledge managemet research that serves the needs of our XBRL data management services. As such, the goal here is not a broad investigation and explanation of the scenario, only to introduce the core concepts as the development goes, without which understanding the code can be harder.

Disclaimer: I am mostly an engineer, reading and writing working source codes most of the time. When I estimate and optimize the flexibility of an information system, I investigate how the meta information appears in its implementation (configurations, scripts, sources). That foretells the side effects of changes on various levels (used technologies, data to be handled, expected behaviors) and leads to a relatively clear segmentation that I call "Kedves-levels"... :-) Milab-dust is expected to provide wide range of services on the XBRL target system where adapting to different meta information sets (taxonomies) by each state information blocks (business report reporting to the taxonomies) is a declared functional requirement, not just a non-functional requirement that may offer some benefits over time. The scale goes from 0 (traditional programming) to 7, milab-dust aims at level 2 and 4.
